package com.giyeok.jsontype

import com.giyeok.jsontype.util.KotlinCodeWriter
import org.junit.jupiter.api.Test

class JsonTypeCompilerTest {

  fun testCompiler(parsed: JsonTypeAst.Defs) {
    val compiler = JsonTypeCompiler(parsed.pkg?.name)
    compiler.compile(parsed.defs)

    val writer = KotlinCodeWriter()
    compiler.classes.forEach { (name, cls) ->
      JsonClassCodeGen(writer).generateClass(cls)
      JsonClassCodeGen(writer).generateClassReader(cls, "", cls.defaultReader)
    }
    compiler.classReaders.forEach { (clsName, readers) ->
      val cls = compiler.classes.getValue(clsName)
      readers.forEach { (readerName, reader) ->
        JsonClassCodeGen(writer).generateClassReader(cls, readerName, reader.body)
      }
    }
    println(writer.toString())
  }

  @Test
  fun test() {
    val parsed = JsonTypeParser.parse(
      """
        type UpbitMarket = com.giyeok.jsontype.tests.UpbitMarket
        
        UpbitMinuteCandle {
          market | mkt: UpbitMarket,
          opening_price as open: BigDecimal,
          "high_price" as high: BigDecimal,
          "low_price" as low: BigDecimal,
          "trade_price" as close: BigDecimal,
          subObj: {
            hello as hi: String,
          },
          list: [Int],
        }
        
        UpbitOrderBook2 {
          "market" | "cd" as market: UpbitMarket,
          "timestamp" | "tms": Long,
          optional var "total_ask_size" | "tas": BigDecimal,
          "total_bid_size" | "tbs": BigDecimal,
          "orderbook_units" | "obu": [OrderBookEntry2 {
            "ask_price" | "ap": BigDecimal,
            "ask_size" | "as": BigDecimal,
            "bid_price" | "bp": BigDecimal,
            "bid_size" | "bs": BigDecimal,
          }],
        }
        
        UpbitOrderBook2::shortForm {
          "cd" as market: UpbitMarket,
          "tms" as timestamp: Long,
          "tas" as totalAskSize: BigDecimal,
          tbs as totalBidSize: BigDecimal,
          obu as orderbookUnits: [OrderBookEntry2::shortForm {
            ap as askPrice: BigDecimal,
            `as` as askSize: BigDecimal,
            bp as bidPrice: BigDecimal,
            bs as bidSize: BigDecimal,
          }],
        }
      """.trimIndent()
    )
    testCompiler(parsed)
  }

  @Test
  fun test2() {
    val parsed = JsonTypeParser.parse(
      """
        BinanceMinuteCandle [
          ...as charts: [BinanceCandleEntry [
            openTime: Long,
            open: BigDecimal,
            high: BigDecimal,
            low: BigDecimal,
            close: BigDecimal,
            volume: BigDecimal,
            closeTime: Long,
            quoteVolume: BigDecimal,
            tradeCount: Long,
            takerBaseVolume: BigDecimal,
            takerQuoteVolume: BigDecimal,
            _ignore: Any,
          ]]
        ]
      """.trimIndent()
    )
    testCompiler(parsed)
  }

  @Test
  fun test3() {
    val parsed = JsonTypeParser.parse(
      """
        TestRest {
          market: String,
          sub: {
            sub1: String
          },
          ... as rest: {[String]: Any},
        }
      """.trimIndent()
    )
    testCompiler(parsed)
  }

  @Test
  fun test4() {
    val parsed = JsonTypeParser.parse(
      """
        type MyEnum = com.giyeok.jsontype.tests.MyEnum
        
        ObjRestTest1 {
          ... as rest: {[String]: String},
        }
        
        ObjMapTest {
          my_obj: {[String]: String},
        }
        
        ArrayRestTest1 [
          ... as rest: [String],
        ]
        
        ArrayTest [
          array: [String],
        ]
      """.trimIndent()
    )
    testCompiler(parsed)
  }
}
