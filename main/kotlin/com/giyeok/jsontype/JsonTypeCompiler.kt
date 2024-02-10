package com.giyeok.jsontype

class JsonTypeCompiler(val pkg: JsonTypeAst.LongName?) {
  val typeDefs = mutableMapOf<String, FieldType>()
  val classes = mutableMapOf<String, JsonClass>()
  val classReaders = mutableMapOf<String, MutableMap<String, JsonClassReader>>()

  fun compile(defs: List<JsonTypeAst.Def>) {
    defs.forEach { def ->
      when (def) {
        is JsonTypeAst.TypeDef -> {
          check(def.name.name !in typeDefs) { "Duplicate type def ${def.name.name}" }
          typeDefs[def.name.name] = compileTypeForTypeDef(def.typ)
        }

        is JsonTypeAst.ClassDef -> compileClass(def)
        is JsonTypeAst.ClassReaderDef -> {
          check(def.name.cls.name in classes) { "Reader definition of unknown class ${def.name.cls.name}" }
          classReaders.getOrPut(def.name.cls.name) { mutableMapOf() }[def.name.reader.name] =
            compileClassReader(def)
        }
      }
    }
  }

  fun compileTypeForTypeDef(typ: JsonTypeAst.Type): FieldType = when (typ) {
    is JsonTypeAst.LongName -> {
      JvmType(typ.names.joinToString(".") { it.name })
    }

    is JsonTypeAst.ArrayType -> TODO()
    is JsonTypeAst.ClassDef -> TODO()
    is JsonTypeAst.ClassReaderDef -> TODO()
    is JsonTypeAst.ClassReaderName -> TODO()
    is JsonTypeAst.ObjDef -> TODO()
    is JsonTypeAst.UnionType -> TODO()
  }

  fun compileClass(def: JsonTypeAst.ClassDef): JsonClass {
    check(def.name.name !in classes) { "Duplicate class def ${def.name.name}" }
    val compiled = JsonClassCompiler().compile(def)
    classes[def.name.name] = compiled
    typeDefs[def.name.name] = JsonClassType(def.name.name, "")
    return compiled
  }

  fun compileClassReader(def: JsonTypeAst.ClassReaderDef): JsonClassReader {
    val compiler = JsonClassCompiler()
    val compiled = compiler.compile(def)
    classReaders.getOrPut(def.name.cls.name) { mutableMapOf() }[def.name.reader.name] = compiled
    return compiled
  }

  inner class JsonClassCompiler {
    val fields = mutableListOf<JsonObjectField>()
    val fieldMap = mutableMapOf<String, JsonObjectField>()

    fun compile(cls: JsonTypeAst.ClassDef): JsonClass {
      val reader = compileObjDef(cls.name.name, cls.def)
      return JsonClass(cls.name.name, fields, reader)
    }

    fun compile(def: JsonTypeAst.ClassReaderDef): JsonClassReader {
      val reader = compileObjDef(def.name.cls.name, def.def)
      return JsonClassReader(def.name.cls.name, def.name.reader.name, reader)
    }

    fun JsonTypeAst.StringLiteral.value(): String = this.chars.map { c ->
      when (c) {
        is JsonTypeAst.Character -> c.value
        is JsonTypeAst.EscapeCode -> when (c.code) {
          '\\' -> '\\'
          else -> TODO()
        }

        is JsonTypeAst.Unicode -> TODO()
      }
    }.toCharArray().concatToString()

    fun JsonTypeAst.ObjFieldNames.toStrings(): List<String> = this.names.map {
      when (it) {
        is JsonTypeAst.Name -> it.name
        is JsonTypeAst.StringLiteral -> it.value()
      }
    }

    private fun addField(field: JsonObjectField) {
      fields.add(field)
      fieldMap[field.kotlinName] = field
    }

    private fun compileObjDef(clsName: String, objDef: JsonTypeAst.ObjDef): JsonObject {
      val objFields = mutableListOf<JsonObjectField>()
      val subObjs = mutableListOf<JsonSubObject>()
      objDef.fields.forEach { field ->
        val kotlinName = field.nameAs?.name ?: field.objFieldName.names.first().toKtName()
        check(kotlinName !in fieldMap) { "Duplicate field name: $kotlinName in $clsName" }

        when (val type = field.typ) {
          is JsonTypeAst.ObjDef -> {
            val subObjDef = compileObjDef(clsName, type)
            subObjs.add(JsonSubObject(field.objFieldName.toStrings(), subObjDef))
          }

          else -> {
            val objField = JsonObjectField(
              kotlinName = kotlinName,
              jsonNames = field.objFieldName.toStrings(),
              isOptional = field.isOptional,
              isVar = field.isVar,
              type = compileType(type)
            )
            objFields.add(objField)
            addField(objField)
          }
        }
      }
      return JsonObject(objFields, subObjs, null)
    }

    fun compileType(type: JsonTypeAst.Type): FieldType = when (type) {
      is JsonTypeAst.LongName ->
        when (val name = type.names.single().name) {
          "Boolean" -> BooleanType
          "Int" -> IntType
          "Long" -> LongType
          "BigInteger" -> BigIntegerType
          "BigDecimal" -> BigDecimalType
          "String" -> StringType
          "Float" -> FloatType
          "Double" -> DoubleType
          else -> {
            check(name in typeDefs) { "Unknown name: $name" }
            typeDefs.getValue(name)
          }
        }

      is JsonTypeAst.ArrayType -> ArrayType(compileType(type.elemType))
      is JsonTypeAst.ClassDef ->
        JsonClassType(compileClass(type).name, "")

      is JsonTypeAst.ClassReaderDef -> {
        val reader = compileClassReader(type)
        JsonClassType(reader.className, reader.readerName)
      }

      is JsonTypeAst.ClassReaderName -> {
        // TODO check if the reader name is valid
        JsonClassType(type.cls.name, type.reader.name)
      }

      is JsonTypeAst.UnionType -> TODO()
      is JsonTypeAst.ObjDef -> throw AssertionError()
    }

    fun String.toLowerCamelCase(): String {
      val tokens = this.split('_')
      return (tokens.take(1) + tokens.drop(1)
        .map { tok -> tok.replaceFirstChar { it.uppercase() } }).joinToString("")
    }

    fun JsonTypeAst.ObjFieldName.toKtName(): String = when (this) {
      is JsonTypeAst.Name -> this.name.toLowerCamelCase()
      is JsonTypeAst.StringLiteral -> this.value().toLowerCamelCase()
    }
  }
}
