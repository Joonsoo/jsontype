package com.giyeok.jsontype

data class JsonTypeDefs(
  val classes: Map<String, JsonClass>,
  val readers: Map<String, Map<String, JsonClassReader>>,
)

data class JsonClass(
  val name: String,
  val fields: List<JsonObjectField>,
  val defaultReader: JsonObject,
) {
}

data class JsonObject(
  val fields: List<JsonObjectField>,
  val subObjs: List<JsonSubObject>,
  val rest: Pair<String, FieldType>?,
)

data class JsonSubObject(
  val jsonNames: List<String>,
  val subObj: JsonObject,
)

data class JsonClassReader(
  val className: String,
  val readerName: String,
  val body: JsonObject,
)

data class JsonObjectField(
  val kotlinName: String,
  val jsonNames: List<String>,
  val isOptional: Boolean,
  val isVar: Boolean,
  val type: FieldType
) {

}

sealed class FieldType

data object IntType: FieldType()
data object LongType: FieldType()
data object BigIntegerType: FieldType()
data object BigDecimalType: FieldType()
data object StringType: FieldType()
data class JsonClassType(val className: String, val readerName: String): FieldType()
data class ArrayType(val elemType: FieldType): FieldType()
data class JvmType(val qualifiedName: String): FieldType() {
  val className get() = qualifiedName.substringAfterLast('.')
}
