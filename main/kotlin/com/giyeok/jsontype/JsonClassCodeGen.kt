package com.giyeok.jsontype

import com.giyeok.jsontype.util.KotlinCodeWriter
import com.giyeok.jsontype.util.KtImport
import java.math.BigDecimal

class JsonClassCodeGen(val writer: KotlinCodeWriter) {
  private var varIdCounter = 0

  fun generateClass(cls: JsonClass) {
    writer.writeLine("data class ${cls.name}(")
    writer.indent {
      cls.fields.forEach { field ->
        val valOrVar = if (field.isVar) "var" else "val"
        val type0 = generateFieldTypeStr(field.type)
        val type = if (field.isOptional) "$type0?" else type0
        writer.writeLine("$valOrVar ${field.kotlinName}: $type,")
      }
    }
    writer.writeLine(")")
  }

  fun generateClassReader(
    cls: JsonClass,
    readerName: String,
    body: JsonObject,
  ) {
    writer.addImport(KtImport("com.google.gson.Gson"))
    writer.addImport(KtImport("com.google.gson.TypeAdapter"))
    writer.addImport(KtImport("com.google.gson.stream.JsonReader"))
    writer.addImport(KtImport("com.google.gson.stream.JsonWriter"))
    val typeAdapterName = typeAdapterNameOf(cls.name, readerName)
    writer.writeLine("class $typeAdapterName(val gson: Gson): TypeAdapter<${cls.name}>() {")
    writer.indent {
      writer.writeLine("companion object {")
      writer.indent {
        writer.writeLine("val FACTORY = object: TypeAdapterFactory {")
        writer.indent {
          writer.writeLine("override fun <T: Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {")
          writer.indent {
            writer.writeLine("if (type.rawType == ${cls.name}::class.java) {")
            writer.indent {
              writer.writeLine("return $typeAdapterName(gson) as TypeAdapter<T>")
            }
            writer.writeLine("}")
            writer.writeLine("return null")
          }
          writer.writeLine("}")
        }
        writer.writeLine("}")
      }
      writer.writeLine("}")
      writer.writeLine()
      writer.writeLine("override fun read(reader: JsonReader): ${cls.name} {")
      writer.indent {
        cls.fields.forEach { field ->
          writer.writeLine("var _${field.kotlinName}: ${generateFieldTypeStr(field.type)}? = null")
          if (field.isOptional) {
            writer.writeLine("var set_${field.kotlinName}: Boolean = false")
          }
        }
        writer.writeLine()
        generateObjectReader(body)
        writer.writeLine()
        writer.writeLine("return ${cls.name}(")
        writer.indent {
          cls.fields.forEach { field ->
            if (field.isOptional) {
              writer.writeLine("${field.kotlinName} = _${field.kotlinName},")
            } else {
              writer.writeLine("${field.kotlinName} = checkNotNull(_${field.kotlinName}) { \"required field not set: ${field.kotlinName}\" },")
            }
          }
        }
        writer.writeLine(")")
      }
      writer.writeLine("}")
      writer.writeLine()
      writer.writeLine("override fun write(writer: JsonWriter, value: ${cls.name}) {")
      writer.indent {
        generateObjectWriter(body)
      }
      writer.writeLine("}")
    }
    writer.writeLine("}")
  }

  fun generateObjectReader(obj: JsonObject) {
    varIdCounter += 1
    val nameVar = "name$varIdCounter"

    writer.writeLine("reader.beginObject()")
    writer.writeLine("while (reader.hasNext()) {")
    writer.indent {
      writer.writeLine("when (val $nameVar = reader.nextName()) {")
      writer.indent {
        obj.fields.forEach { field ->
          // TODO escape
          writer.writeLine("${field.jsonNames.joinToString { "\"$it\"" }} -> {")
          writer.indent {
            generateFieldReader(field)
          }
          writer.writeLine("}")
        }

        obj.subObjs.forEach { subObj ->
          writer.writeLine("${subObj.jsonNames.joinToString { "\"$it\"" }} -> {")
          writer.indent {
            generateObjectReader(subObj.subObj)
          }
          writer.writeLine("}")
        }

        writer.writeLine("else -> {")
        writer.indent {
          if (obj.rest == null) {
            writer.writeLine("throw IllegalStateException(\"Unknown field: \$$nameVar\")")
          } else {
            TODO()
          }
        }
        writer.writeLine("}")
      }
      writer.writeLine("}")
    }
    writer.writeLine("}")
    writer.writeLine("reader.endObject()")
  }

  fun generateFieldReader(field: JsonObjectField) {
    if (field.isOptional) {
      // 값이 null인 경우 처리
      writer.addImport(KtImport("com.google.gson.stream.JsonToken"))
      writer.writeLine("check(!set_${field.kotlinName}) { \"Duplicate field: ${field.kotlinName}\" }")
      writer.writeLine("set_${field.kotlinName} = true")
      writer.writeLine("if (reader.peek() != JsonToken.NULL) {")
      writer.writeLineIndented("_${field.kotlinName} = ${generateValueReader(field.type)}")
      writer.writeLine("}")
    } else {
      writer.writeLine("check(_${field.kotlinName} == null) { \"Duplicate field: ${field.kotlinName}\" }")
      writer.writeLine("_${field.kotlinName} = ${generateValueReader(field.type)}")
    }
  }

  fun typeAdapterNameOf(clsName: String, readerName: String): String =
    "${clsName}${readerName.replaceFirstChar { it.uppercase() }}Adapter"

  fun generateValueReader(type: FieldType): String = when (type) {
    BooleanType -> "reader.nextBool()"
    IntType -> "reader.nextInt()"
    LongType -> "reader.nextLong()"
    BigIntegerType -> "gson.fromJson(reader, BigInteger::class.java)"
    BigDecimalType -> "gson.fromJson(reader, BigDecimal::class.java)"
    StringType -> "reader.nextString()"
    FloatType -> "reader.nextFloat()"
    DoubleType -> "reader.nextDouble()"

    is ArrayType -> {
      varIdCounter += 1
      val arrayVar = "array$varIdCounter"
      writer.writeLine("val $arrayVar = mutableListOf<${generateFieldTypeStr(type.elemType)}>()")
      writer.writeLine("reader.beginArray()")
      writer.writeLine("while (reader.hasNext()) {")
      writer.indent {
        val nextElem = generateValueReader(type.elemType)
        writer.writeLine("$arrayVar.add($nextElem)")
      }
      writer.writeLine("}")
      writer.writeLine("reader.endArray()")
      arrayVar
    }

    is JsonClassType -> {
      "${typeAdapterNameOf(type.className, type.readerName)}(gson).read(reader)"
    }

    is JvmType -> {
      val jvmType = type.qualifiedName
      writer.addImport(KtImport(jvmType))
      "gson.fromJson(reader, ${jvmType.substringAfterLast('.')}::class.java)"
    }
  }

  fun generateFieldTypeStr(type: FieldType): String = when (type) {
    BooleanType -> "Boolean"
    IntType -> "Int"
    LongType -> "Long"
    StringType -> "String"
    FloatType -> "Float"
    DoubleType -> "Double"
    BigIntegerType -> {
      writer.addImport(KtImport("java.math.BigInteger"))
      "BigInteger"
    }

    BigDecimalType -> {
      writer.addImport(KtImport("java.math.BigDecimal"))
      "BigDecimal"
    }

    is ArrayType -> {
      "List<${generateFieldTypeStr(type.elemType)}>"
    }

    is JsonClassType -> type.className
    is JvmType -> {
      writer.addImport(KtImport(type.qualifiedName))
      type.className
    }
  }

  fun generateBuilderTypeStr(type: FieldType): String = when (type) {
    BooleanType -> "Boolean"
    IntType -> "Int"
    LongType -> "Long"
    StringType -> "String"
    FloatType -> "Float"
    DoubleType -> "Double"
    BigIntegerType -> {
      writer.addImport(KtImport("java.math.BigInteger"))
      "BigInteger"
    }

    BigDecimalType -> {
      writer.addImport(KtImport("java.math.BigDecimal"))
      "BigDecimal"
    }

    is ArrayType -> {
      "MutableList<${generateFieldTypeStr(type.elemType)}>"
    }

    is JsonClassType -> type.className
    is JvmType -> {
      writer.addImport(KtImport(type.qualifiedName))
      type.className
    }
  }

  fun generateObjectWriter(obj: JsonObject) {
    writer.writeLine("writer.beginObject()")
    obj.fields.forEach { field ->
      if (field.isOptional) {
        writer.writeLine("if (value.${field.kotlinName} != null) {")
        writer.indent {
          // TODO escape
          writer.writeLine("writer.name(\"${field.jsonNames.first()}\")")
          generateFieldWriter(field)
        }
        writer.writeLine("}")
      } else {
        // TODO escape
        writer.writeLine("writer.name(\"${field.jsonNames.first()}\")")
        generateFieldWriter(field)
      }
    }
    writer.writeLine("writer.endObject()")
  }

  fun generateFieldWriter(field: JsonObjectField) {
    generateValueWriter("value.${field.kotlinName}", field.type)
  }

  fun generateValueWriter(value: String, type: FieldType) {
    when (type) {
      BooleanType,
      IntType,
      LongType,
      StringType,
      FloatType,
      DoubleType -> writer.writeLine("writer.value($value)")

      BigIntegerType -> {
        writer.addImport(KtImport("java.math.BigInteger"))
        writer.writeLine("gson.toJson($value, BigInteger::class.java, writer)")
      }

      BigDecimalType -> {
        writer.addImport(KtImport("java.math.BigDecimal"))
        writer.writeLine("gson.toJson($value, BigDecimal::class.java, writer)")
      }

      is JvmType -> {
        writer.addImport(KtImport(type.qualifiedName))
        writer.writeLine("gson.toJson($value, ${type.className}::class.java, writer)")
      }

      is ArrayType -> {
        varIdCounter += 1
        val elemVar = "elem$varIdCounter"

        writer.writeLine("writer.beginArray()")
        writer.writeLine("$value.forEach { $elemVar ->")
        writer.indent {
          generateValueWriter(elemVar, type.elemType)
        }
        writer.writeLine("}")
        writer.writeLine("writer.endArray()")
      }

      is JsonClassType -> {
        val typeAdapterName = typeAdapterNameOf(type.className, type.readerName)
        writer.writeLine("$typeAdapterName(gson).write(writer, $value)")
      }
    }
  }
}
