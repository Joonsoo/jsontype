package com.giyeok.jsontype

import com.giyeok.jsontype.util.KotlinCodeWriter
import com.giyeok.jsontype.util.KtImport

class JsonClassCodeGen(val writer: KotlinCodeWriter) {
  private var varIdCounter = 0

  fun generateClass(cls: JsonClass) {
    writer.writeLine("data class ${cls.name}(")
    writer.indent {
      (cls.fields).forEach { field ->
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
    body: JsonReader,
  ) {
    writer.addImport(KtImport("com.google.gson.Gson"))
    writer.addImport(KtImport("com.google.gson.TypeAdapter"))
    writer.addImport(KtImport("com.google.gson.stream.JsonReader"))
    writer.addImport(KtImport("com.google.gson.stream.JsonWriter"))
    val typeAdapterName = typeAdapterNameOf(cls.name, readerName)
    writer.writeLine("class $typeAdapterName(val gson: Gson): TypeAdapter<${cls.name}>() {")
    writer.addImport(KtImport("com.google.gson.TypeAdapterFactory"))
    writer.addImport(KtImport("com.google.gson.reflect.TypeToken"))
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
          if (field.isRest) {
            val fieldInit = when (field.type) {
              is StringToValueType ->
                "mutableMapOf<String, ${generateFieldTypeStr(field.type.valueType)}>()"

              is ArrayType ->
                "mutableListOf<${generateFieldTypeStr(field.type.elemType)}>()"

              else -> throw AssertionError()
            }
            writer.writeLine("val _${field.kotlinName} = $fieldInit")
          } else {
            writer.writeLine("var _${field.kotlinName}: ${generateFieldTypeStr(field.type)}? = null")
            if (field.isOptional) {
              writer.writeLine("var set_${field.kotlinName}: Boolean = false")
            }
          }
        }

        generateReader(body)
        writer.writeLine("return ${cls.name}(")
        writer.indent {
          cls.fields.forEach { field ->
            if (field.isOptional || field.isRest) {
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
        generateWriter(body)
      }
      writer.writeLine("}")
    }
    writer.writeLine("}")
  }

  fun generateObjectReader(obj: JsonObjectReader) {
    varIdCounter += 1
    val nameVar = "name$varIdCounter"

    writer.writeLine("reader.beginObject()")
    writer.writeLine("while (reader.hasNext()) {")
    writer.indent {
      writer.writeLine("when (val $nameVar = reader.nextName()) {")
      writer.indent {
        obj.fields.forEach { field ->
          writer.writeLine("${field.jsonNames.joinToString { strLiteral(it) }} -> {")
          writer.indent {
            generateFieldReader(field)
          }
          writer.writeLine("}")
        }

        obj.subObjs.forEach { subObj ->
          writer.writeLine("${subObj.jsonNames.joinToString { strLiteral(it) }} -> {")
          writer.indent {
            generateReader(subObj.sub)
          }
          writer.writeLine("}")
        }

        writer.writeLine("else -> {")
        writer.indent {
          if (obj.rest == null) {
            writer.writeLine("throw IllegalStateException(\"Unknown field: \$$nameVar\")")
          } else {
            if (obj.rest.name == null && obj.rest.fieldType == null) {
              // 그냥 있어도 OK인 것이므로 무시하고 지나감
              writer.writeLine(generateValueReader(JsonElemType))
            } else {
              checkNotNull(obj.rest.fieldType) { "rest field type is mandatory" }
              val valueReader = generateValueReader(obj.rest.fieldType)
              writer.writeLine("check($nameVar !in _${obj.rest.name}) { \"Duplicate field: $nameVar\" }")
              writer.writeLine("_${obj.rest.name}[$nameVar] = $valueReader")
            }
          }
        }
        writer.writeLine("}")
      }
      writer.writeLine("}")
    }
    writer.writeLine("}")
    writer.writeLine("reader.endObject()")
  }

  fun generateArrayReader(obj: JsonArrayReader) {
    writer.writeLine("reader.beginArray()")
    obj.elems.forEach { elem ->
      when (elem) {
        is JsonObjectField -> {
          generateFieldReader(elem)
        }

        is JsonSubReader ->
          generateReader(elem.sub)
      }
    }

    obj.rest?.let { rest ->
      if (rest.name == null) {
        // 배열 끝까지 그냥 consume
        TODO()
      } else {
        // restVarName은 reader body 첫부분에서 이미 정의되었음
        val restVarName = "_${rest.name}"
        checkNotNull(rest.fieldType)
        writer.writeLine("while (reader.hasNext()) {")
        writer.indent {
          val value = generateValueReader(rest.fieldType)
          writer.writeLine("$restVarName.add($value)")
        }
        writer.writeLine("}")
      }
    }
    writer.writeLine("reader.endArray()")
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
    BooleanType -> "reader.nextBoolean()"
    IntType -> "reader.nextInt()"
    LongType -> "reader.nextLong()"
    BigIntegerType -> "gson.fromJson(reader, BigInteger::class.java)"
    BigDecimalType -> "gson.fromJson(reader, BigDecimal::class.java)"
    StringType -> "reader.nextString()"
    FloatType -> "reader.nextFloat()"
    DoubleType -> "reader.nextDouble()"
    JsonElemType -> {
      writer.addImport(KtImport("com.google.gson.JsonElement"))
      "gson.fromJson(reader, JsonElement::class.java)"
    }

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

    is StringToValueType -> {
      // TODO 이 코드가 실행될 수가 있나?
      varIdCounter += 1
      val mapVar = "map$varIdCounter"
      val valueType = generateFieldTypeStr(type.valueType)
      writer.writeLine("val $mapVar = mutableMapOf<String, $valueType>()")
      writer.writeLine("reader.beginArray()")
      writer.writeLine("while (reader.hasNext()) {")
      writer.indent {
        TODO()
      }
      writer.writeLine("}")
      writer.writeLine("reader.endArray()")
      mapVar
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
    JsonElemType -> {
      writer.addImport(KtImport("com.google.gson.JsonElement"))
      "JsonElement"
    }

    BigIntegerType -> {
      writer.addImport(KtImport("java.math.BigInteger"))
      "BigInteger"
    }

    BigDecimalType -> {
      writer.addImport(KtImport("java.math.BigDecimal"))
      "BigDecimal"
    }

    is ArrayType -> "List<${generateFieldTypeStr(type.elemType)}>"
    is StringToValueType -> "Map<String, ${generateFieldTypeStr(type.valueType)}>"

    is JsonClassType -> type.className
    is JvmType -> {
      writer.addImport(KtImport(type.qualifiedName))
      type.className
    }
  }

  fun generateReader(reader: JsonReader) {
    when (reader) {
      is JsonObjectReader -> generateObjectReader(reader)
      is JsonArrayReader -> generateArrayReader(reader)
    }
  }

  fun generateWriter(reader: JsonReader) {
    when (reader) {
      is JsonObjectReader -> generateObjectWriter(reader)
      is JsonArrayReader -> generateArrayWriter(reader)
    }
  }

  fun strLiteral(value: String) = "\"$value\""

  fun generateObjectWriter(obj: JsonObjectReader) {
    writer.writeLine("writer.beginObject()")
    obj.fields.forEach { field ->
      if (field.isOptional) {
        writer.writeLine("if (value.${field.kotlinName} != null) {")
        writer.indent {
          writer.writeLine("writer.name(${strLiteral(field.jsonNames.first())})")
          generateFieldWriter(field)
        }
        writer.writeLine("}")
      } else {
        writer.writeLine("writer.name(${strLiteral(field.jsonNames.first())})")
        generateFieldWriter(field)
      }
    }
    obj.subObjs.forEach { subObj ->
      writer.writeLine("writer.name(${strLiteral(subObj.jsonNames.first())})")
      generateWriter(subObj.sub)
    }
    obj.rest?.let { rest ->
      if (rest.name != null) {
        checkNotNull(rest.fieldType)
        varIdCounter += 1
        val keyVar = "key$varIdCounter"
        val valVar = "value$varIdCounter"
        writer.writeLine("value.${rest.name}.forEach { $keyVar, $valVar ->")
        writer.indent {
          writer.writeLine("writer.name($keyVar)")
          generateValueWriter(valVar, rest.fieldType)
        }
        writer.writeLine("}")
      }
    }
    writer.writeLine("writer.endObject()")
  }

  fun generateArrayWriter(obj: JsonArrayReader) {
    writer.writeLine("writer.beginArray()")
    obj.elems.forEach { elem ->
      when (elem) {
        is JsonObjectField -> {
          if (elem.isOptional) {
            writer.writeLine("if (value.${elem.kotlinName} != null) {")
            writer.indent {
              generateFieldWriter(elem)
            }
            writer.writeLine("}")
          } else {
            generateFieldWriter(elem)
          }
        }

        is JsonSubReader -> TODO()
      }
    }
    obj.rest?.let { rest ->
      if (rest.name != null) {
        varIdCounter += 1
        val elemVar = "elem$varIdCounter"
        writer.writeLine("value.${rest.name}.forEach { $elemVar ->")
        writer.indent {
          generateValueWriter(elemVar, rest.fieldType!!)
        }
        writer.writeLine("}")
      }
    }
    writer.writeLine("writer.endArray()")
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

      JsonElemType -> writer.writeLine("gson.toJson($value, writer)")

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

      is StringToValueType -> {
        varIdCounter += 1
        val keyVar = "key$varIdCounter"
        val valueVar = "val$varIdCounter"

        writer.writeLine("writer.beginObject()")
        writer.writeLine("$value.forEach { ($keyVar, $valueVar) ->")
        writer.indent {
          writer.writeLine("writer.name($keyVar)")
          generateValueWriter(valueVar, type.valueType)
        }
        writer.writeLine("}")
        writer.writeLine("writer.endObject()")
      }

      is JsonClassType -> {
        val typeAdapterName = typeAdapterNameOf(type.className, type.readerName)
        writer.writeLine("$typeAdapterName(gson).write(writer, $value)")
      }
    }
  }
}
