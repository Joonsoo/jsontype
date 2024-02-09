package com.giyeok.jsontype

import com.giyeok.jparser.ktlib.*

class JsonTypeAst(
  val source: String,
  val history: List<KernelSet>,
  val idIssuer: IdIssuer = IdIssuerImpl(0)
) {
  private fun nextId(): Int = idIssuer.nextId()

  sealed interface AstNode {
    val nodeId: Int
    val start: Int
    val end: Int
  }

sealed interface CharElem: AstNode

data class Name(
  val name: String,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): ObjFieldName, AstNode

data class Defs(
  val pkg: Package?,
  val imports: List<Import>,
  val defs: List<Def>,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): AstNode

data class StringLiteral(
  val chars: List<CharElem>,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): ObjFieldName, AstNode

data class ClassReaderDef(
  val name: ClassReaderName,
  val def: ObjDef,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): Def, NonUnionType, AstNode

data class Unicode(
  val hexcode: String,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): CharElem, AstNode

data class ArrayType(
  val elemType: Type,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): NonUnionType, AstNode

sealed interface ObjFieldName: AstNode

data class Character(
  val value: Char,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): CharElem, AstNode

data class Package(
  val name: LongName,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): AstNode

data class Import(
  val filepath: StringLiteral,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): AstNode

data class UnionType(
  val types: List<NonUnionType>,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): Type, AstNode

sealed interface Def: AstNode

data class EscapeCode(
  val code: Char,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): CharElem, AstNode

sealed interface NonUnionType: Type, AstNode

data class ObjRest(
  val name: Name?,
  val typ: Type?,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): AstNode

data class ObjFieldNames(
  val names: List<ObjFieldName>,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): AstNode

sealed interface Type: AstNode

data class LongName(
  val names: List<Name>,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): NonUnionType, AstNode

data class ObjDef(
  val fields: List<ObjField>,
  val rest: ObjRest?,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): NonUnionType, AstNode

data class ClassDef(
  val name: Name,
  val def: ObjDef,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): Def, NonUnionType, AstNode

data class ClassReaderName(
  val cls: Name,
  val reader: Name,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): NonUnionType, AstNode

data class TypeDef(
  val name: Name,
  val typ: Type,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): Def, AstNode

data class ObjField(
  val isOptional: Boolean,
  val isVar: Boolean,
  val objFieldName: ObjFieldNames,
  val nameAs: Name?,
  val typ: Type,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): AstNode


fun matchStart(): Defs {
  val lastGen = source.length
  val kernel = history[lastGen].getSingle(2, 1, 0, lastGen)
  return matchDefs(kernel.beginGen, kernel.endGen)
}

fun matchDefs(beginGen: Int, endGen: Int): Defs {
val var1 = getSequenceElems(history, 3, listOf(4,104,127,7), beginGen, endGen)
val var2 = history[var1[0].second].findByBeginGenOpt(5, 1, var1[0].first)
val var3 = history[var1[0].second].findByBeginGenOpt(44, 1, var1[0].first)
check(hasSingleTrue(var2 != null, var3 != null))
val var4 = when {
var2 != null -> {
val var5 = getSequenceElems(history, 6, listOf(7,45), var1[0].first, var1[0].second)
val var6 = matchPackage(var5[1].first, var5[1].second)
var6
}
else -> null
}
val var7 = unrollRepeat0(history, 104, 106, 9, 105, var1[1].first, var1[1].second).map { k ->
val var8 = getSequenceElems(history, 107, listOf(7,108), k.first, k.second)
val var9 = matchImport(var8[1].first, var8[1].second)
var9
}
val var10 = unrollRepeat0(history, 127, 129, 9, 128, var1[2].first, var1[2].second).map { k ->
val var11 = getSequenceElems(history, 130, listOf(7,131), k.first, k.second)
val var12 = matchDef(var11[1].first, var11[1].second)
var12
}
val var13 = Defs(var4, var7, var10, nextId(), beginGen, endGen)
return var13
}

fun matchPackage(beginGen: Int, endGen: Int): Package {
val var14 = getSequenceElems(history, 46, listOf(47,7,65), beginGen, endGen)
val var15 = matchLongName(var14[2].first, var14[2].second)
val var16 = Package(var15, nextId(), beginGen, endGen)
return var16
}

fun matchLongName(beginGen: Int, endGen: Int): LongName {
val var17 = getSequenceElems(history, 66, listOf(67,99), beginGen, endGen)
val var18 = matchName(var17[0].first, var17[0].second)
val var19 = unrollRepeat0(history, 99, 101, 9, 100, var17[1].first, var17[1].second).map { k ->
val var20 = getSequenceElems(history, 102, listOf(7,103,7,67), k.first, k.second)
val var21 = matchName(var20[3].first, var20[3].second)
var21
}
val var22 = LongName(listOf(var18) + var19, nextId(), beginGen, endGen)
return var22
}

fun matchName(beginGen: Int, endGen: Int): Name {
val var23 = history[endGen].findByBeginGenOpt(68, 1, beginGen)
val var24 = history[endGen].findByBeginGenOpt(93, 3, beginGen)
check(hasSingleTrue(var23 != null, var24 != null))
val var25 = when {
var23 != null -> {
val var26 = getSequenceElems(history, 60, listOf(61,62), beginGen, endGen)
val var27 = unrollRepeat0(history, 62, 64, 9, 63, var26[1].first, var26[1].second).map { k ->
source[k.first]
}
val var28 = Name(source[var26[0].first].toString() + var27.joinToString("") { it.toString() }, nextId(), beginGen, endGen)
var28
}
else -> {
val var29 = getSequenceElems(history, 93, listOf(94,95,94), beginGen, endGen)
val var30 = unrollRepeat0(history, 95, 97, 9, 96, var29[1].first, var29[1].second).map { k ->
source[k.first]
}
val var31 = Name(var30.joinToString("") { it.toString() }, nextId(), beginGen, endGen)
var31
}
}
return var25
}

fun matchImport(beginGen: Int, endGen: Int): Import {
val var32 = getSequenceElems(history, 109, listOf(110,7,112), beginGen, endGen)
val var33 = matchStringLiteral(var32[2].first, var32[2].second)
val var34 = Import(var33, nextId(), beginGen, endGen)
return var34
}

fun matchStringLiteral(beginGen: Int, endGen: Int): StringLiteral {
val var35 = getSequenceElems(history, 113, listOf(114,115,114), beginGen, endGen)
val var36 = unrollRepeat0(history, 115, 117, 9, 116, var35[1].first, var35[1].second).map { k ->
val var37 = matchCharElem(k.first, k.second)
var37
}
val var38 = StringLiteral(var36, nextId(), beginGen, endGen)
return var38
}

fun matchCharElem(beginGen: Int, endGen: Int): CharElem {
val var39 = history[endGen].findByBeginGenOpt(118, 1, beginGen)
val var40 = history[endGen].findByBeginGenOpt(120, 2, beginGen)
val var41 = history[endGen].findByBeginGenOpt(123, 6, beginGen)
check(hasSingleTrue(var39 != null, var40 != null, var41 != null))
val var42 = when {
var39 != null -> {
val var43 = Character(source[beginGen], nextId(), beginGen, endGen)
var43
}
var40 != null -> {
val var44 = getSequenceElems(history, 120, listOf(121,122), beginGen, endGen)
val var45 = EscapeCode(source[var44[1].first], nextId(), beginGen, endGen)
var45
}
else -> {
val var46 = getSequenceElems(history, 123, listOf(121,124,125,125,125,125), beginGen, endGen)
val var47 = matchHex(var46[2].first, var46[2].second)
val var48 = matchHex(var46[3].first, var46[3].second)
val var49 = matchHex(var46[4].first, var46[4].second)
val var50 = matchHex(var46[5].first, var46[5].second)
val var51 = Unicode(var47.toString() + var48.toString() + var49.toString() + var50.toString(), nextId(), beginGen, endGen)
var51
}
}
return var42
}

fun matchHex(beginGen: Int, endGen: Int): Char {
return source[beginGen]
}

fun matchDef(beginGen: Int, endGen: Int): Def {
val var52 = history[endGen].findByBeginGenOpt(132, 1, beginGen)
val var53 = history[endGen].findByBeginGenOpt(139, 1, beginGen)
val var54 = history[endGen].findByBeginGenOpt(192, 1, beginGen)
check(hasSingleTrue(var52 != null, var53 != null, var54 != null))
val var55 = when {
var52 != null -> {
val var56 = matchTypeDef(beginGen, endGen)
var56
}
var53 != null -> {
val var57 = matchClassDef(beginGen, endGen)
var57
}
else -> {
val var58 = matchClassReaderDef(beginGen, endGen)
var58
}
}
return var55
}

fun matchClassDef(beginGen: Int, endGen: Int): ClassDef {
val var59 = getSequenceElems(history, 140, listOf(67,7,141), beginGen, endGen)
val var60 = matchName(var59[0].first, var59[0].second)
val var61 = matchObjDef(var59[2].first, var59[2].second)
val var62 = ClassDef(var60, var61, nextId(), beginGen, endGen)
return var62
}

fun matchObjDef(beginGen: Int, endGen: Int): ObjDef {
val var64 = getSequenceElems(history, 142, listOf(143,144,181,7,191), beginGen, endGen)
val var65 = history[var64[1].second].findByBeginGenOpt(44, 1, var64[1].first)
val var66 = history[var64[1].second].findByBeginGenOpt(145, 1, var64[1].first)
check(hasSingleTrue(var65 != null, var66 != null))
val var67 = when {
var65 != null -> null
else -> {
val var68 = getSequenceElems(history, 146, listOf(7,147,173,178), var64[1].first, var64[1].second)
val var69 = matchObjField(var68[1].first, var68[1].second)
val var70 = unrollRepeat0(history, 173, 175, 9, 174, var68[2].first, var68[2].second).map { k ->
val var71 = getSequenceElems(history, 176, listOf(7,177,7,147), k.first, k.second)
val var72 = matchObjField(var71[3].first, var71[3].second)
var72
}
listOf(var69) + var70
}
}
val var63 = var67
val var73 = history[var64[2].second].findByBeginGenOpt(44, 1, var64[2].first)
val var74 = history[var64[2].second].findByBeginGenOpt(182, 1, var64[2].first)
check(hasSingleTrue(var73 != null, var74 != null))
val var75 = when {
var73 != null -> null
else -> {
val var76 = getSequenceElems(history, 183, listOf(7,184), var64[2].first, var64[2].second)
val var77 = matchObjRest(var76[1].first, var76[1].second)
var77
}
}
val var78 = ObjDef((var63 ?: listOf()), var75, nextId(), beginGen, endGen)
return var78
}

fun matchClassReaderDef(beginGen: Int, endGen: Int): ClassReaderDef {
val var79 = getSequenceElems(history, 193, listOf(194,7,141), beginGen, endGen)
val var80 = matchClassReaderName(var79[0].first, var79[0].second)
val var81 = matchObjDef(var79[2].first, var79[2].second)
val var82 = ClassReaderDef(var80, var81, nextId(), beginGen, endGen)
return var82
}

fun matchClassReaderName(beginGen: Int, endGen: Int): ClassReaderName {
val var83 = getSequenceElems(history, 195, listOf(67,7,196,7,67), beginGen, endGen)
val var84 = matchName(var83[0].first, var83[0].second)
val var85 = matchName(var83[4].first, var83[4].second)
val var86 = ClassReaderName(var84, var85, nextId(), beginGen, endGen)
return var86
}

fun matchObjRest(beginGen: Int, endGen: Int): ObjRest {
val var87 = getSequenceElems(history, 185, listOf(186,167,188), beginGen, endGen)
val var88 = history[var87[1].second].findByBeginGenOpt(44, 1, var87[1].first)
val var89 = history[var87[1].second].findByBeginGenOpt(168, 1, var87[1].first)
check(hasSingleTrue(var88 != null, var89 != null))
val var90 = when {
var88 != null -> null
else -> {
val var91 = getSequenceElems(history, 169, listOf(7,170,7,67), var87[1].first, var87[1].second)
val var92 = matchName(var91[3].first, var91[3].second)
var92
}
}
val var93 = history[var87[2].second].findByBeginGenOpt(44, 1, var87[2].first)
val var94 = history[var87[2].second].findByBeginGenOpt(189, 1, var87[2].first)
check(hasSingleTrue(var93 != null, var94 != null))
val var95 = when {
var93 != null -> null
else -> {
val var96 = getSequenceElems(history, 190, listOf(7,172,7,137), var87[2].first, var87[2].second)
val var97 = matchType(var96[3].first, var96[3].second)
var97
}
}
val var98 = ObjRest(var90, var95, nextId(), beginGen, endGen)
return var98
}

fun matchType(beginGen: Int, endGen: Int): Type {
val var99 = history[endGen].findByBeginGenOpt(138, 1, beginGen)
val var100 = history[endGen].findByBeginGenOpt(202, 1, beginGen)
check(hasSingleTrue(var99 != null, var100 != null))
val var101 = when {
var99 != null -> {
val var102 = matchNonUnionType(beginGen, endGen)
var102
}
else -> {
val var103 = matchUnionType(beginGen, endGen)
var103
}
}
return var101
}

fun matchUnionType(beginGen: Int, endGen: Int): UnionType {
val var104 = getSequenceElems(history, 203, listOf(138,204), beginGen, endGen)
val var105 = matchNonUnionType(var104[0].first, var104[0].second)
val var106 = unrollRepeat1(history, 204, 205, 205, 207, var104[1].first, var104[1].second).map { k ->
val var107 = getSequenceElems(history, 206, listOf(7,166,7,138), k.first, k.second)
val var108 = matchNonUnionType(var107[3].first, var107[3].second)
var108
}
val var109 = UnionType(listOf(var105) + var106, nextId(), beginGen, endGen)
return var109
}

fun matchNonUnionType(beginGen: Int, endGen: Int): NonUnionType {
val var110 = history[endGen].findByBeginGenOpt(65, 1, beginGen)
val var111 = history[endGen].findByBeginGenOpt(139, 1, beginGen)
val var112 = history[endGen].findByBeginGenOpt(141, 1, beginGen)
val var113 = history[endGen].findByBeginGenOpt(192, 1, beginGen)
val var114 = history[endGen].findByBeginGenOpt(194, 1, beginGen)
val var115 = history[endGen].findByBeginGenOpt(198, 1, beginGen)
check(hasSingleTrue(var110 != null, var111 != null, var112 != null, var113 != null, var114 != null, var115 != null))
val var116 = when {
var110 != null -> {
val var117 = matchLongName(beginGen, endGen)
var117
}
var111 != null -> {
val var118 = matchClassDef(beginGen, endGen)
var118
}
var112 != null -> {
val var119 = matchObjDef(beginGen, endGen)
var119
}
var113 != null -> {
val var120 = matchClassReaderDef(beginGen, endGen)
var120
}
var114 != null -> {
val var121 = matchClassReaderName(beginGen, endGen)
var121
}
else -> {
val var122 = matchArrayType(beginGen, endGen)
var122
}
}
return var116
}

fun matchArrayType(beginGen: Int, endGen: Int): ArrayType {
val var123 = getSequenceElems(history, 199, listOf(200,7,137,7,201), beginGen, endGen)
val var124 = matchType(var123[2].first, var123[2].second)
val var125 = ArrayType(var124, nextId(), beginGen, endGen)
return var125
}

fun matchTypeDef(beginGen: Int, endGen: Int): TypeDef {
val var126 = getSequenceElems(history, 133, listOf(134,7,67,7,136,7,137), beginGen, endGen)
val var127 = matchName(var126[2].first, var126[2].second)
val var128 = matchType(var126[6].first, var126[6].second)
val var129 = TypeDef(var127, var128, nextId(), beginGen, endGen)
return var129
}

fun matchObjField(beginGen: Int, endGen: Int): ObjField {
val var130 = getSequenceElems(history, 148, listOf(149,154,159,167,7,172,7,137), beginGen, endGen)
val var131 = history[var130[0].second].findByBeginGenOpt(44, 1, var130[0].first)
val var132 = history[var130[0].second].findByBeginGenOpt(150, 1, var130[0].first)
check(hasSingleTrue(var131 != null, var132 != null))
val var133 = when {
var131 != null -> null
else -> {
val var134 = getSequenceElems(history, 151, listOf(152,7), var130[0].first, var130[0].second)
val var135 = matchWS(var134[1].first, var134[1].second)
var135
}
}
val var136 = history[var130[1].second].findByBeginGenOpt(44, 1, var130[1].first)
val var137 = history[var130[1].second].findByBeginGenOpt(155, 1, var130[1].first)
check(hasSingleTrue(var136 != null, var137 != null))
val var138 = when {
var136 != null -> null
else -> {
val var139 = getSequenceElems(history, 156, listOf(157,7), var130[1].first, var130[1].second)
val var140 = matchWS(var139[1].first, var139[1].second)
var140
}
}
val var141 = matchObjFieldNames(var130[2].first, var130[2].second)
val var142 = history[var130[3].second].findByBeginGenOpt(44, 1, var130[3].first)
val var143 = history[var130[3].second].findByBeginGenOpt(168, 1, var130[3].first)
check(hasSingleTrue(var142 != null, var143 != null))
val var144 = when {
var142 != null -> null
else -> {
val var145 = getSequenceElems(history, 169, listOf(7,170,7,67), var130[3].first, var130[3].second)
val var146 = matchName(var145[3].first, var145[3].second)
var146
}
}
val var147 = matchType(var130[7].first, var130[7].second)
val var148 = ObjField(var133 != null, var138 != null, var141, var144, var147, nextId(), beginGen, endGen)
return var148
}

fun matchWS(beginGen: Int, endGen: Int): String {
return ""
}

fun matchObjFieldNames(beginGen: Int, endGen: Int): ObjFieldNames {
val var149 = getSequenceElems(history, 160, listOf(161,162), beginGen, endGen)
val var150 = matchObjFieldName(var149[0].first, var149[0].second)
val var151 = unrollRepeat0(history, 162, 164, 9, 163, var149[1].first, var149[1].second).map { k ->
val var152 = getSequenceElems(history, 165, listOf(7,166,7,161), k.first, k.second)
val var153 = matchObjFieldName(var152[3].first, var152[3].second)
var153
}
val var154 = ObjFieldNames(listOf(var150) + var151, nextId(), beginGen, endGen)
return var154
}

fun matchObjFieldName(beginGen: Int, endGen: Int): ObjFieldName {
val var155 = history[endGen].findByBeginGenOpt(67, 1, beginGen)
val var156 = history[endGen].findByBeginGenOpt(112, 1, beginGen)
check(hasSingleTrue(var155 != null, var156 != null))
val var157 = when {
var155 != null -> {
val var158 = matchName(beginGen, endGen)
var158
}
else -> {
val var159 = matchStringLiteral(beginGen, endGen)
var159
}
}
return var157
}

}
