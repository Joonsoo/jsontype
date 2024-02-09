package com.giyeok.jsontype

import org.junit.jupiter.api.Test

class JsonTypeParserTest {
  @Test
  fun test() {
    val parsed = JsonTypeParser.parse(
      """
        UpbitMinuteCandle {
          market: UpbitMarket.bySymbol,
          "candle_date_time_utc": LocalDateTime,
          "candle_date_time_kst": LocalDateTime,
          opening_price as open: BigDecimal,
          "high_price" as high: BigDecimal,
          "low_price" as low: BigDecimal,
          "trade_price" as close: BigDecimal,
          "timestamp": Long | String,
          "candle_acc_trade_price": BigDecimal,
          "candle_acc_trade_volume": BigDecimal,
          "unit": Double,
          "as" as `as`: Int,
        }

        UpbitOrderBook {
          "market" | "cd" as market: UpbitMarket,
          "timestamp" | "tms": Long,
          optional var "total_ask_size" | "tas": BigDecimal,
          "total_bid_size" | "tbs": BigDecimal,
          "orderbook_units" | "obu": [OrderBookEntry {
            "ask_price" | "ap": BigDecimal,
            "ask_size" | "as": BigDecimal,
            "bid_price" | "bp": BigDecimal,
            "bid_size" | "bs": BigDecimal,
          }],
        }

        UpbitOrderBook::longForm {
          "market": UpbitMarket,
          "timestamp": Long,
          "total_ask_size": BigDecimal,
          "total_bid_size": BigDecimal,
          "orderbook_units": [OrderBookEntry {
            "ask_price": BigDecimal,
            "ask_size": BigDecimal,
            "bid_price": BigDecimal,
            "bid_size": BigDecimal,
          }],
        }

        UpbitOrderBook::shortForm {
          "cd" as market: UpbitMarket,
          "tms": Long,
          "tas": BigDecimal,
          "tbs": BigDecimal,
          "obu": [OrderBookEntry::shortForm {
            "ap": BigDecimal,
            "as": BigDecimal,
            "bp": BigDecimal,
            "bs": BigDecimal,
          }],
          "obu": [OrderBookEntry],
          "obu": [OrderBookEntry::shortForm],
        }

        MapType {
          "a": Long,
          "b": [[Long]],
          ... as rest: ValueType
        }
      """.trimIndent()
    )
    println(parsed)
  }

  @Test
  fun test2() {
    val parsed = JsonTypeParser.parse(
      """
        CoinoneOrderBook {
          result: String,
          error_code: String,
          timestamp: Long,
          id: String,
          quote_currency: CoinoneAsset.byCode,
          target_currency: CoinoneAsset.byCode,
          order_book_unit: BigDecimal,
          bids: [OrderBookEntry {
            price: BigDecimal,
            qty as quantity: BigDecimal,
          }],
          asks: [OrderBookEntry]
        }
        
        CoinoneMinuteCandle {
          result: String,
          error_code: String,
          is_last: Boolean,
          chart: [CoinoneMinuteCandleEntry {
            timestamp: Long,
            open: BigDecimal,
            high: BigDecimal,
            low: BigDecimal,
            close: BigDecimal,
            target_volume: BigDecimal,
            quote_volume: BigDecimal,
          }],
        }
      """.trimIndent()
    )
    println(parsed)
  }
}
