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

data class ObjObjDef(
  val fields: List<ObjField>,
  val rest: ObjRest?,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): ObjDef, AstNode

sealed interface NonUnionType: Type, AstNode

data class ObjRest(
  val name: Name?,
  val typ: Type?,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): AstNode

data class LongName(
  val names: List<Name>,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): NonUnionType, AstNode

sealed interface ObjDef: NonUnionType, AstNode

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

data class ObjFieldNames(
  val names: List<ObjFieldName>,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): AstNode

data class ArrayObjDef(
  val fields: List<ObjField>,
  val rest: ObjRest?,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): ObjDef, AstNode

sealed interface Type: AstNode

data class MapType(
  val key: Type,
  val value: Type,
  override val nodeId: Int,
  override val start: Int,
  override val end: Int,
): NonUnionType, AstNode


fun matchStart(): Defs {
  val lastGen = source.length
  val kernel = history[lastGen].getSingle(2, 1, 0, lastGen)
  return matchDefs(kernel.beginGen, kernel.endGen)
}

fun matchDefs(beginGen: Int, endGen: Int): Defs {
val var1 = getSequenceElems(history, 3, listOf(4,114,137,7), beginGen, endGen)
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
val var7 = unrollRepeat0(history, 114, 116, 9, 115, var1[1].first, var1[1].second).map { k ->
val var8 = getSequenceElems(history, 117, listOf(7,118), k.first, k.second)
val var9 = matchImport(var8[1].first, var8[1].second)
var9
}
val var10 = unrollRepeat0(history, 137, 139, 9, 138, var1[2].first, var1[2].second).map { k ->
val var11 = getSequenceElems(history, 140, listOf(7,141), k.first, k.second)
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
val var17 = getSequenceElems(history, 66, listOf(67,109), beginGen, endGen)
val var18 = matchName(var17[0].first, var17[0].second)
val var19 = unrollRepeat0(history, 109, 111, 9, 110, var17[1].first, var17[1].second).map { k ->
val var20 = getSequenceElems(history, 112, listOf(7,113,7,67), k.first, k.second)
val var21 = matchName(var20[3].first, var20[3].second)
var21
}
val var22 = LongName(listOf(var18) + var19, nextId(), beginGen, endGen)
return var22
}

fun matchName(beginGen: Int, endGen: Int): Name {
val var23 = history[endGen].findByBeginGenOpt(68, 1, beginGen)
val var24 = history[endGen].findByBeginGenOpt(103, 3, beginGen)
check(hasSingleTrue(var23 != null, var24 != null))
val var25 = when {
var23 != null -> {
val var26 = history[endGen].findByBeginGenOpt(72, 1, beginGen)
val var27 = history[endGen].findByBeginGenOpt(75, 1, beginGen)
check(hasSingleTrue(var26 != null, var27 != null))
val var28 = when {
var26 != null -> {
val var29 = getSequenceElems(history, 73, listOf(74,62), beginGen, endGen)
val var30 = unrollRepeat0(history, 62, 64, 9, 63, var29[1].first, var29[1].second).map { k ->
source[k.first]
}
source[var29[0].first].toString() + var30.joinToString("") { it.toString() }
}
else -> {
val var31 = getSequenceElems(history, 76, listOf(77,78), beginGen, endGen)
val var32 = unrollRepeat1(history, 78, 64, 64, 79, var31[1].first, var31[1].second).map { k ->
source[k.first]
}
source[var31[0].first].toString() + var32.joinToString("") { it.toString() }
}
}
val var33 = Name(var28, nextId(), beginGen, endGen)
var33
}
else -> {
val var34 = getSequenceElems(history, 103, listOf(104,105,104), beginGen, endGen)
val var35 = unrollRepeat0(history, 105, 107, 9, 106, var34[1].first, var34[1].second).map { k ->
source[k.first]
}
val var36 = Name(var35.joinToString("") { it.toString() }, nextId(), beginGen, endGen)
var36
}
}
return var25
}

fun matchImport(beginGen: Int, endGen: Int): Import {
val var37 = getSequenceElems(history, 119, listOf(120,7,122), beginGen, endGen)
val var38 = matchStringLiteral(var37[2].first, var37[2].second)
val var39 = Import(var38, nextId(), beginGen, endGen)
return var39
}

fun matchStringLiteral(beginGen: Int, endGen: Int): StringLiteral {
val var40 = getSequenceElems(history, 123, listOf(124,125,124), beginGen, endGen)
val var41 = unrollRepeat0(history, 125, 127, 9, 126, var40[1].first, var40[1].second).map { k ->
val var42 = matchCharElem(k.first, k.second)
var42
}
val var43 = StringLiteral(var41, nextId(), beginGen, endGen)
return var43
}

fun matchCharElem(beginGen: Int, endGen: Int): CharElem {
val var44 = history[endGen].findByBeginGenOpt(128, 1, beginGen)
val var45 = history[endGen].findByBeginGenOpt(130, 2, beginGen)
val var46 = history[endGen].findByBeginGenOpt(133, 6, beginGen)
check(hasSingleTrue(var44 != null, var45 != null, var46 != null))
val var47 = when {
var44 != null -> {
val var48 = Character(source[beginGen], nextId(), beginGen, endGen)
var48
}
var45 != null -> {
val var49 = getSequenceElems(history, 130, listOf(131,132), beginGen, endGen)
val var50 = EscapeCode(source[var49[1].first], nextId(), beginGen, endGen)
var50
}
else -> {
val var51 = getSequenceElems(history, 133, listOf(131,134,135,135,135,135), beginGen, endGen)
val var52 = matchHex(var51[2].first, var51[2].second)
val var53 = matchHex(var51[3].first, var51[3].second)
val var54 = matchHex(var51[4].first, var51[4].second)
val var55 = matchHex(var51[5].first, var51[5].second)
val var56 = Unicode(var52.toString() + var53.toString() + var54.toString() + var55.toString(), nextId(), beginGen, endGen)
var56
}
}
return var47
}

fun matchHex(beginGen: Int, endGen: Int): Char {
return source[beginGen]
}

fun matchDef(beginGen: Int, endGen: Int): Def {
val var57 = history[endGen].findByBeginGenOpt(142, 1, beginGen)
val var58 = history[endGen].findByBeginGenOpt(149, 1, beginGen)
val var59 = history[endGen].findByBeginGenOpt(205, 1, beginGen)
check(hasSingleTrue(var57 != null, var58 != null, var59 != null))
val var60 = when {
var57 != null -> {
val var61 = matchTypeDef(beginGen, endGen)
var61
}
var58 != null -> {
val var62 = matchClassDef(beginGen, endGen)
var62
}
else -> {
val var63 = matchClassReaderDef(beginGen, endGen)
var63
}
}
return var60
}

fun matchClassDef(beginGen: Int, endGen: Int): ClassDef {
val var64 = getSequenceElems(history, 150, listOf(67,7,151), beginGen, endGen)
val var65 = matchName(var64[0].first, var64[0].second)
val var66 = matchObjDef(var64[2].first, var64[2].second)
val var67 = ClassDef(var65, var66, nextId(), beginGen, endGen)
return var67
}

fun matchObjDef(beginGen: Int, endGen: Int): ObjDef {
val var68 = history[endGen].findByBeginGenOpt(152, 5, beginGen)
val var69 = history[endGen].findByBeginGenOpt(202, 5, beginGen)
check(hasSingleTrue(var68 != null, var69 != null))
val var70 = when {
var68 != null -> {
val var72 = getSequenceElems(history, 152, listOf(153,154,191,7,201), beginGen, endGen)
val var73 = history[var72[1].second].findByBeginGenOpt(44, 1, var72[1].first)
val var74 = history[var72[1].second].findByBeginGenOpt(155, 1, var72[1].first)
check(hasSingleTrue(var73 != null, var74 != null))
val var75 = when {
var73 != null -> null
else -> {
val var76 = getSequenceElems(history, 156, listOf(7,157,183,188), var72[1].first, var72[1].second)
val var77 = matchObjField(var76[1].first, var76[1].second)
val var78 = unrollRepeat0(history, 183, 185, 9, 184, var76[2].first, var76[2].second).map { k ->
val var79 = getSequenceElems(history, 186, listOf(7,187,7,157), k.first, k.second)
val var80 = matchObjField(var79[3].first, var79[3].second)
var80
}
listOf(var77) + var78
}
}
val var71 = var75
val var81 = history[var72[2].second].findByBeginGenOpt(44, 1, var72[2].first)
val var82 = history[var72[2].second].findByBeginGenOpt(192, 1, var72[2].first)
check(hasSingleTrue(var81 != null, var82 != null))
val var83 = when {
var81 != null -> null
else -> {
val var84 = getSequenceElems(history, 193, listOf(7,194,188), var72[2].first, var72[2].second)
val var85 = matchObjRest(var84[1].first, var84[1].second)
var85
}
}
val var86 = ObjObjDef((var71 ?: listOf()), var83, nextId(), beginGen, endGen)
var86
}
else -> {
val var88 = getSequenceElems(history, 202, listOf(203,154,191,7,204), beginGen, endGen)
val var89 = history[var88[1].second].findByBeginGenOpt(44, 1, var88[1].first)
val var90 = history[var88[1].second].findByBeginGenOpt(155, 1, var88[1].first)
check(hasSingleTrue(var89 != null, var90 != null))
val var91 = when {
var89 != null -> null
else -> {
val var92 = getSequenceElems(history, 156, listOf(7,157,183,188), var88[1].first, var88[1].second)
val var93 = matchObjField(var92[1].first, var92[1].second)
val var94 = unrollRepeat0(history, 183, 185, 9, 184, var92[2].first, var92[2].second).map { k ->
val var95 = getSequenceElems(history, 186, listOf(7,187,7,157), k.first, k.second)
val var96 = matchObjField(var95[3].first, var95[3].second)
var96
}
listOf(var93) + var94
}
}
val var87 = var91
val var97 = history[var88[2].second].findByBeginGenOpt(44, 1, var88[2].first)
val var98 = history[var88[2].second].findByBeginGenOpt(192, 1, var88[2].first)
check(hasSingleTrue(var97 != null, var98 != null))
val var99 = when {
var97 != null -> null
else -> {
val var100 = getSequenceElems(history, 193, listOf(7,194,188), var88[2].first, var88[2].second)
val var101 = matchObjRest(var100[1].first, var100[1].second)
var101
}
}
val var102 = ArrayObjDef((var87 ?: listOf()), var99, nextId(), beginGen, endGen)
var102
}
}
return var70
}

fun matchClassReaderDef(beginGen: Int, endGen: Int): ClassReaderDef {
val var103 = getSequenceElems(history, 206, listOf(207,7,151), beginGen, endGen)
val var104 = matchClassReaderName(var103[0].first, var103[0].second)
val var105 = matchObjDef(var103[2].first, var103[2].second)
val var106 = ClassReaderDef(var104, var105, nextId(), beginGen, endGen)
return var106
}

fun matchClassReaderName(beginGen: Int, endGen: Int): ClassReaderName {
val var107 = getSequenceElems(history, 208, listOf(67,7,209,7,67), beginGen, endGen)
val var108 = matchName(var107[0].first, var107[0].second)
val var109 = matchName(var107[4].first, var107[4].second)
val var110 = ClassReaderName(var108, var109, nextId(), beginGen, endGen)
return var110
}

fun matchObjRest(beginGen: Int, endGen: Int): ObjRest {
val var111 = getSequenceElems(history, 195, listOf(196,177,198), beginGen, endGen)
val var112 = history[var111[1].second].findByBeginGenOpt(44, 1, var111[1].first)
val var113 = history[var111[1].second].findByBeginGenOpt(178, 1, var111[1].first)
check(hasSingleTrue(var112 != null, var113 != null))
val var114 = when {
var112 != null -> null
else -> {
val var115 = getSequenceElems(history, 179, listOf(7,180,7,67), var111[1].first, var111[1].second)
val var116 = matchName(var115[3].first, var115[3].second)
var116
}
}
val var117 = history[var111[2].second].findByBeginGenOpt(44, 1, var111[2].first)
val var118 = history[var111[2].second].findByBeginGenOpt(199, 1, var111[2].first)
check(hasSingleTrue(var117 != null, var118 != null))
val var119 = when {
var117 != null -> null
else -> {
val var120 = getSequenceElems(history, 200, listOf(7,182,7,147), var111[2].first, var111[2].second)
val var121 = matchType(var120[3].first, var120[3].second)
var121
}
}
val var122 = ObjRest(var114, var119, nextId(), beginGen, endGen)
return var122
}

fun matchType(beginGen: Int, endGen: Int): Type {
val var123 = history[endGen].findByBeginGenOpt(148, 1, beginGen)
val var124 = history[endGen].findByBeginGenOpt(215, 1, beginGen)
check(hasSingleTrue(var123 != null, var124 != null))
val var125 = when {
var123 != null -> {
val var126 = matchNonUnionType(beginGen, endGen)
var126
}
else -> {
val var127 = matchUnionType(beginGen, endGen)
var127
}
}
return var125
}

fun matchUnionType(beginGen: Int, endGen: Int): UnionType {
val var128 = getSequenceElems(history, 216, listOf(148,217), beginGen, endGen)
val var129 = matchNonUnionType(var128[0].first, var128[0].second)
val var130 = unrollRepeat1(history, 217, 218, 218, 220, var128[1].first, var128[1].second).map { k ->
val var131 = getSequenceElems(history, 219, listOf(7,176,7,148), k.first, k.second)
val var132 = matchNonUnionType(var131[3].first, var131[3].second)
var132
}
val var133 = UnionType(listOf(var129) + var130, nextId(), beginGen, endGen)
return var133
}

fun matchNonUnionType(beginGen: Int, endGen: Int): NonUnionType {
val var134 = history[endGen].findByBeginGenOpt(65, 1, beginGen)
val var135 = history[endGen].findByBeginGenOpt(149, 1, beginGen)
val var136 = history[endGen].findByBeginGenOpt(151, 1, beginGen)
val var137 = history[endGen].findByBeginGenOpt(205, 1, beginGen)
val var138 = history[endGen].findByBeginGenOpt(207, 1, beginGen)
val var139 = history[endGen].findByBeginGenOpt(211, 1, beginGen)
val var140 = history[endGen].findByBeginGenOpt(213, 1, beginGen)
check(hasSingleTrue(var134 != null, var135 != null, var136 != null, var137 != null, var138 != null, var139 != null, var140 != null))
val var141 = when {
var134 != null -> {
val var142 = matchLongName(beginGen, endGen)
var142
}
var135 != null -> {
val var143 = matchClassDef(beginGen, endGen)
var143
}
var136 != null -> {
val var144 = matchObjDef(beginGen, endGen)
var144
}
var137 != null -> {
val var145 = matchClassReaderDef(beginGen, endGen)
var145
}
var138 != null -> {
val var146 = matchClassReaderName(beginGen, endGen)
var146
}
var139 != null -> {
val var147 = matchArrayType(beginGen, endGen)
var147
}
else -> {
val var148 = matchMapType(beginGen, endGen)
var148
}
}
return var141
}

fun matchArrayType(beginGen: Int, endGen: Int): ArrayType {
val var149 = getSequenceElems(history, 212, listOf(203,7,147,7,204), beginGen, endGen)
val var150 = matchType(var149[2].first, var149[2].second)
val var151 = ArrayType(var150, nextId(), beginGen, endGen)
return var151
}

fun matchMapType(beginGen: Int, endGen: Int): MapType {
val var152 = getSequenceElems(history, 214, listOf(153,7,203,7,147,7,204,7,182,7,147,7,201), beginGen, endGen)
val var153 = matchType(var152[4].first, var152[4].second)
val var154 = matchType(var152[10].first, var152[10].second)
val var155 = MapType(var153, var154, nextId(), beginGen, endGen)
return var155
}

fun matchTypeDef(beginGen: Int, endGen: Int): TypeDef {
val var156 = getSequenceElems(history, 143, listOf(144,7,67,7,146,7,147), beginGen, endGen)
val var157 = matchName(var156[2].first, var156[2].second)
val var158 = matchType(var156[6].first, var156[6].second)
val var159 = TypeDef(var157, var158, nextId(), beginGen, endGen)
return var159
}

fun matchObjField(beginGen: Int, endGen: Int): ObjField {
val var160 = getSequenceElems(history, 158, listOf(159,164,169,177,7,182,7,147), beginGen, endGen)
val var161 = history[var160[0].second].findByBeginGenOpt(44, 1, var160[0].first)
val var162 = history[var160[0].second].findByBeginGenOpt(160, 1, var160[0].first)
check(hasSingleTrue(var161 != null, var162 != null))
val var163 = when {
var161 != null -> null
else -> {
val var164 = getSequenceElems(history, 161, listOf(162,7), var160[0].first, var160[0].second)
val var165 = matchWS(var164[1].first, var164[1].second)
var165
}
}
val var166 = history[var160[1].second].findByBeginGenOpt(44, 1, var160[1].first)
val var167 = history[var160[1].second].findByBeginGenOpt(165, 1, var160[1].first)
check(hasSingleTrue(var166 != null, var167 != null))
val var168 = when {
var166 != null -> null
else -> {
val var169 = getSequenceElems(history, 166, listOf(167,7), var160[1].first, var160[1].second)
val var170 = matchWS(var169[1].first, var169[1].second)
var170
}
}
val var171 = matchObjFieldNames(var160[2].first, var160[2].second)
val var172 = history[var160[3].second].findByBeginGenOpt(44, 1, var160[3].first)
val var173 = history[var160[3].second].findByBeginGenOpt(178, 1, var160[3].first)
check(hasSingleTrue(var172 != null, var173 != null))
val var174 = when {
var172 != null -> null
else -> {
val var175 = getSequenceElems(history, 179, listOf(7,180,7,67), var160[3].first, var160[3].second)
val var176 = matchName(var175[3].first, var175[3].second)
var176
}
}
val var177 = matchType(var160[7].first, var160[7].second)
val var178 = ObjField(var163 != null, var168 != null, var171, var174, var177, nextId(), beginGen, endGen)
return var178
}

fun matchWS(beginGen: Int, endGen: Int): String {
return ""
}

fun matchObjFieldNames(beginGen: Int, endGen: Int): ObjFieldNames {
val var179 = getSequenceElems(history, 170, listOf(171,172), beginGen, endGen)
val var180 = matchObjFieldName(var179[0].first, var179[0].second)
val var181 = unrollRepeat0(history, 172, 174, 9, 173, var179[1].first, var179[1].second).map { k ->
val var182 = getSequenceElems(history, 175, listOf(7,176,7,171), k.first, k.second)
val var183 = matchObjFieldName(var182[3].first, var182[3].second)
var183
}
val var184 = ObjFieldNames(listOf(var180) + var181, nextId(), beginGen, endGen)
return var184
}

fun matchObjFieldName(beginGen: Int, endGen: Int): ObjFieldName {
val var185 = history[endGen].findByBeginGenOpt(67, 1, beginGen)
val var186 = history[endGen].findByBeginGenOpt(122, 1, beginGen)
check(hasSingleTrue(var185 != null, var186 != null))
val var187 = when {
var185 != null -> {
val var188 = matchName(beginGen, endGen)
var188
}
else -> {
val var189 = matchStringLiteral(beginGen, endGen)
var189
}
}
return var187
}

}
