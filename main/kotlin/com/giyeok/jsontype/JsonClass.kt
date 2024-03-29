package com.giyeok.jsontype

data class JsonTypeDefs(
  val classes: Map<String, JsonClass>,
  val readers: Map<String, Map<String, JsonClassReader>>,
)

data class JsonClass(
  val name: String,
  val fields: List<JsonObjectField>,
  val defaultReader: JsonReader,
)

sealed class JsonReader

data class JsonObjectReader(
  val fields: List<JsonObjectField>,
  val subObjs: List<JsonSubReader>,
  val rest: JsonObjRest?,
): JsonReader()

sealed class JsonObjRest {
  abstract val restType: MapType?
}

data class JsonObjDisposeRest(override val restType: MapType?): JsonObjRest()
data class JsonObjRestField(val name: String, override val restType: MapType): JsonObjRest()

data class JsonArrayReader(
  val elems: List<JsonArrayElem>,
  val rest: JsonArrayRest?,
): JsonReader()

sealed class JsonArrayRest
data class JsonArrayDisposeRest(val restType: ArrayType?): JsonArrayRest()
data class JsonArrayRestField(val name: String, val restType: ArrayType): JsonArrayRest()

sealed class JsonArrayElem

data class JsonSubReader(
  val jsonNames: List<String>,
  val sub: JsonReader,
): JsonArrayElem()

data class JsonObjectField(
  val isRest: Boolean,
  val kotlinName: String,
  val jsonNames: List<String>,
  val isOptional: Boolean,
  val isVar: Boolean,
  val type: FieldType
): JsonArrayElem()

data class JsonClassReader(
  val className: String,
  val readerName: String,
  val body: JsonReader,
)

sealed class FieldType

data object BooleanType: FieldType()
data object IntType: FieldType()
data object LongType: FieldType()
data object BigIntegerType: FieldType()
data object BigDecimalType: FieldType()
data object StringType: FieldType()
data object FloatType: FieldType()
data object DoubleType: FieldType()
data object JsonElemType: FieldType()
data class JsonClassType(val className: String, val readerName: String): FieldType()
data class ArrayType(val elemType: FieldType): FieldType()
data class MapType(val keyType: FieldType, val valueType: FieldType): FieldType()
data class JvmType(val qualifiedName: String): FieldType() {
  val className get() = qualifiedName.substringAfterLast('.')
}
