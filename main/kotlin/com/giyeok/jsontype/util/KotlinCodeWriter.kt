package com.giyeok.jsontype.util

class KotlinCodeWriter(
  val packageName: String? = null,
  val publicProtoPackageName: String? = null,
  val internalProtoPackageName: String? = null,
): CodeWriter() {
  private val imports: MutableList<KtImport> = mutableListOf()

  fun addImport(import: KtImport) {
    imports.add(import)
  }

  override fun clear() {
    super.clear()
    imports.clear()
  }

  private fun String.withPackageName(packageName: String?): String =
    if (packageName == null) this else "$packageName.$this"

  // TODO recordName이 internal record이면 internal로 반환
  private fun protoPackageNameForRecord(recordName: String): String? = publicProtoPackageName
  private fun protoPackageNameForEnum(enumName: String): String? = publicProtoPackageName
  private fun protoPackageNameForView(entityName: String, recordName: String): String? =
    publicProtoPackageName

  override fun toString(): String {
    val result = StringBuilder()
    if (packageName != null) {
      result.append("package $packageName\n\n")
    }
    if (imports.isNotEmpty()) {
      imports.sorted().distinct().forEach { import ->
        result.append("import ${import.name}\n")
      }
      result.append("\n")
    }
    result.append(super.toString())
    return result.toString()
  }
}

data class KtImport(val name: String): Comparable<KtImport> {
  override fun compareTo(other: KtImport): Int = this.name.compareTo(other.name)
}
