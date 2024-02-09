package com.giyeok.jsontype

import com.giyeok.jparser.ktparser.mgroup2.MilestoneGroupParserLoader
import com.giyeok.jsontype.JsonTypeAst

object JsonTypeParser {
  val parser = MilestoneGroupParserLoader.loadParserFromResource("/jsontype-mg2-parserdata.pb")

  fun parse(source: String): JsonTypeAst.Defs {
    val result = parser.parse(source)
    val history = parser.kernelsHistory(result)
    return JsonTypeAst(source, history).matchStart()
  }
}
