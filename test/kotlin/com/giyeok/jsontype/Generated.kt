package com.giyeok.jsontype

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.math.BigDecimal
import java.math.BigInteger

enum class UpbitMarket {
  BTCKRW
}

data class UpbitMinuteCandle(
  val market: UpbitMarket,
  val open: BigDecimal,
  val high: BigDecimal,
  val low: BigDecimal,
  val close: BigDecimal,
  val hi: String,
  val list: List<Int>,
)

class UpbitMinuteCandleAdapter(val gson: Gson): TypeAdapter<UpbitMinuteCandle>() {
  override fun read(reader: JsonReader): UpbitMinuteCandle {
    var _market: UpbitMarket? = null
    var set_market: Boolean = false
    var _open: BigDecimal? = null
    var set_open: Boolean = false
    var _high: BigDecimal? = null
    var set_high: Boolean = false
    var _low: BigDecimal? = null
    var set_low: Boolean = false
    var _close: BigDecimal? = null
    var set_close: Boolean = false
    var _hi: String? = null
    var set_hi: Boolean = false
    var _list: List<Int>? = null
    var set_list: Boolean = false

    reader.beginObject()
    while (reader.hasNext()) {
      when (val name1 = reader.nextName()) {
        "market", "mkt" -> {
          check(!set_market) { "Duplicate field: market" }
          set_market = true
          _market = gson.fromJson(reader, UpbitMarket::class.java)
        }

        "opening_price" -> {
          check(!set_open) { "Duplicate field: open" }
          set_open = true
          _open = gson.fromJson(reader, BigDecimal::class.java)
        }

        "high_price" -> {
          check(!set_high) { "Duplicate field: high" }
          set_high = true
          _high = gson.fromJson(reader, BigDecimal::class.java)
        }

        "low_price" -> {
          check(!set_low) { "Duplicate field: low" }
          set_low = true
          _low = gson.fromJson(reader, BigDecimal::class.java)
        }

        "trade_price" -> {
          check(!set_close) { "Duplicate field: close" }
          set_close = true
          _close = gson.fromJson(reader, BigDecimal::class.java)
        }

        "list" -> {
          check(!set_list) { "Duplicate field: list" }
          set_list = true
          val array2 = mutableListOf<Int>()
          reader.beginArray()
          while (reader.hasNext()) {
            array2.add(reader.nextInt())
          }
          reader.endArray()
          _list = array2
        }

        "subObj" -> {
          reader.beginObject()
          while (reader.hasNext()) {
            when (val name3 = reader.nextName()) {
              "hello" -> {
                check(!set_hi) { "Duplicate field: hi" }
                set_hi = true
                _hi = reader.nextString()
              }

              else -> {
                throw IllegalStateException("Unknown field: $name3")
              }
            }
          }
          reader.endObject()
        }

        else -> {
          throw IllegalStateException("Unknown field: $name1")
        }
      }
    }
    reader.endObject()

    return UpbitMinuteCandle(
      market = checkNotNull(_market) { "required field not set: market" },
      open = checkNotNull(_open) { "required field not set: open" },
      high = checkNotNull(_high) { "required field not set: high" },
      low = checkNotNull(_low) { "required field not set: low" },
      close = checkNotNull(_close) { "required field not set: close" },
      hi = checkNotNull(_hi) { "required field not set: hi" },
      list = checkNotNull(_list) { "required field not set: list" },
    )
  }

  override fun write(writer: JsonWriter, value: UpbitMinuteCandle) {
    writer.beginObject()
    writer.name("market")
    gson.toJson(value.market, UpbitMarket::class.java, writer)
    writer.name("opening_price")
    gson.toJson(value.open, BigDecimal::class.java, writer)
    writer.name("high_price")
    gson.toJson(value.high, BigDecimal::class.java, writer)
    writer.name("low_price")
    gson.toJson(value.low, BigDecimal::class.java, writer)
    writer.name("trade_price")
    gson.toJson(value.close, BigDecimal::class.java, writer)
    writer.name("list")
    writer.beginArray()
    value.list.forEach { elem4 ->
      writer.value(elem4)
    }
    writer.endArray()
    writer.endObject()
  }
}

data class OrderBookEntry2(
  val askPrice: BigDecimal,
  val askSize: BigDecimal,
  val bidPrice: BigDecimal,
  val bidSize: BigDecimal,
)

class OrderBookEntry2Adapter(val gson: Gson): TypeAdapter<OrderBookEntry2>() {
  override fun read(reader: JsonReader): OrderBookEntry2 {
    var _askPrice: BigDecimal? = null
    var set_askPrice: Boolean = false
    var _askSize: BigDecimal? = null
    var set_askSize: Boolean = false
    var _bidPrice: BigDecimal? = null
    var set_bidPrice: Boolean = false
    var _bidSize: BigDecimal? = null
    var set_bidSize: Boolean = false

    reader.beginObject()
    while (reader.hasNext()) {
      when (val name1 = reader.nextName()) {
        "ask_price", "ap" -> {
          check(!set_askPrice) { "Duplicate field: askPrice" }
          set_askPrice = true
          _askPrice = gson.fromJson(reader, BigDecimal::class.java)
        }

        "ask_size", "as" -> {
          check(!set_askSize) { "Duplicate field: askSize" }
          set_askSize = true
          _askSize = gson.fromJson(reader, BigDecimal::class.java)
        }

        "bid_price", "bp" -> {
          check(!set_bidPrice) { "Duplicate field: bidPrice" }
          set_bidPrice = true
          _bidPrice = gson.fromJson(reader, BigDecimal::class.java)
        }

        "bid_size", "bs" -> {
          check(!set_bidSize) { "Duplicate field: bidSize" }
          set_bidSize = true
          _bidSize = gson.fromJson(reader, BigDecimal::class.java)
        }

        else -> {
          throw IllegalStateException("Unknown field: $name1")
        }
      }
    }
    reader.endObject()

    return OrderBookEntry2(
      askPrice = checkNotNull(_askPrice) { "required field not set: askPrice" },
      askSize = checkNotNull(_askSize) { "required field not set: askSize" },
      bidPrice = checkNotNull(_bidPrice) { "required field not set: bidPrice" },
      bidSize = checkNotNull(_bidSize) { "required field not set: bidSize" },
    )
  }

  override fun write(writer: JsonWriter, value: OrderBookEntry2) {
    writer.beginObject()
    writer.name("ask_price")
    gson.toJson(value.askPrice, BigDecimal::class.java, writer)
    writer.name("ask_size")
    gson.toJson(value.askSize, BigDecimal::class.java, writer)
    writer.name("bid_price")
    gson.toJson(value.bidPrice, BigDecimal::class.java, writer)
    writer.name("bid_size")
    gson.toJson(value.bidSize, BigDecimal::class.java, writer)
    writer.endObject()
  }
}

data class UpbitOrderBook2(
  val market: UpbitMarket,
  val timestamp: Long,
  var totalAskSize: BigDecimal?,
  val totalBidSize: BigDecimal,
  val orderbookUnits: List<OrderBookEntry2>,
)

class UpbitOrderBook2Adapter(val gson: Gson): TypeAdapter<UpbitOrderBook2>() {
  companion object {
    val FACTORY = object: TypeAdapterFactory {
      override fun <T: Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType == UpbitOrderBook2::class.java) {
          return UpbitOrderBook2Adapter(gson) as TypeAdapter<T>
        }
        return null
      }
    }
  }

  override fun read(reader: JsonReader): UpbitOrderBook2 {
    var _market: UpbitMarket? = null
    var set_market: Boolean = false
    var _timestamp: Long? = null
    var set_timestamp: Boolean = false
    var _totalAskSize: BigDecimal? = null
    var set_totalAskSize: Boolean = false
    var _totalBidSize: BigDecimal? = null
    var set_totalBidSize: Boolean = false
    var _orderbookUnits: List<OrderBookEntry2>? = null
    var set_orderbookUnits: Boolean = false

    reader.beginObject()
    while (reader.hasNext()) {
      when (val name1 = reader.nextName()) {
        "market", "cd" -> {
          check(!set_market) { "Duplicate field: market" }
          set_market = true
          _market = gson.fromJson(reader, UpbitMarket::class.java)
        }

        "timestamp", "tms" -> {
          check(!set_timestamp) { "Duplicate field: timestamp" }
          set_timestamp = true
          _timestamp = reader.nextLong()
        }

        "total_ask_size", "tas" -> {
          check(!set_totalAskSize) { "Duplicate field: totalAskSize" }
          set_totalAskSize = true
          if (reader.peek() != JsonToken.NULL) {
            _totalAskSize = gson.fromJson(reader, BigDecimal::class.java)
          }
        }

        "total_bid_size", "tbs" -> {
          check(!set_totalBidSize) { "Duplicate field: totalBidSize" }
          set_totalBidSize = true
          _totalBidSize = gson.fromJson(reader, BigDecimal::class.java)
        }

        "orderbook_units", "obu" -> {
          check(!set_orderbookUnits) { "Duplicate field: orderbookUnits" }
          set_orderbookUnits = true
          val array2 = mutableListOf<OrderBookEntry2>()
          reader.beginArray()
          while (reader.hasNext()) {
            array2.add(OrderBookEntry2Adapter(gson).read(reader))
          }
          reader.endArray()
          _orderbookUnits = array2
        }

        else -> {
          throw IllegalStateException("Unknown field: $name1")
        }
      }
    }
    reader.endObject()

    return UpbitOrderBook2(
      market = checkNotNull(_market) { "required field not set: market" },
      timestamp = checkNotNull(_timestamp) { "required field not set: timestamp" },
      totalAskSize = _totalAskSize,
      totalBidSize = checkNotNull(_totalBidSize) { "required field not set: totalBidSize" },
      orderbookUnits = checkNotNull(_orderbookUnits) { "required field not set: orderbookUnits" },
    )
  }

  override fun write(writer: JsonWriter, value: UpbitOrderBook2) {
    writer.beginObject()
    writer.name("market")
    gson.toJson(value.market, UpbitMarket::class.java, writer)
    writer.name("timestamp")
    writer.value(value.timestamp)
    if (value.totalAskSize != null) {
      writer.name("total_ask_size")
      gson.toJson(value.totalAskSize, BigDecimal::class.java, writer)
    }
    writer.name("total_bid_size")
    gson.toJson(value.totalBidSize, BigDecimal::class.java, writer)
    writer.name("orderbook_units")
    writer.beginArray()
    value.orderbookUnits.forEach { elem3 ->
      OrderBookEntry2Adapter(gson).write(writer, elem3)
    }
    writer.endArray()
    writer.endObject()
  }
}

class UpbitOrderBook2ShortFormAdapter(val gson: Gson): TypeAdapter<UpbitOrderBook2>() {
  override fun read(reader: JsonReader): UpbitOrderBook2 {
    var _market: UpbitMarket? = null
    var set_market: Boolean = false
    var _timestamp: Long? = null
    var set_timestamp: Boolean = false
    var _totalAskSize: BigDecimal? = null
    var set_totalAskSize: Boolean = false
    var _totalBidSize: BigDecimal? = null
    var set_totalBidSize: Boolean = false
    var _orderbookUnits: List<OrderBookEntry2>? = null
    var set_orderbookUnits: Boolean = false

    reader.beginObject()
    while (reader.hasNext()) {
      when (val name1 = reader.nextName()) {
        "cd" -> {
          check(!set_market) { "Duplicate field: market" }
          set_market = true
          _market = gson.fromJson(reader, UpbitMarket::class.java)
        }

        "tms" -> {
          check(!set_timestamp) { "Duplicate field: timestamp" }
          set_timestamp = true
          _timestamp = reader.nextLong()
        }

        "tas" -> {
          check(!set_totalAskSize) { "Duplicate field: totalAskSize" }
          set_totalAskSize = true
          _totalAskSize = gson.fromJson(reader, BigDecimal::class.java)
        }

        "tbs" -> {
          check(!set_totalBidSize) { "Duplicate field: totalBidSize" }
          set_totalBidSize = true
          _totalBidSize = gson.fromJson(reader, BigDecimal::class.java)
        }

        "obu" -> {
          check(!set_orderbookUnits) { "Duplicate field: orderbookUnits" }
          set_orderbookUnits = true
          val array2 = mutableListOf<OrderBookEntry2>()
          reader.beginArray()
          while (reader.hasNext()) {
            array2.add(OrderBookEntry2ShortFormAdapter(gson).read(reader))
          }
          reader.endArray()
          _orderbookUnits = array2
        }

        else -> {
          throw IllegalStateException("Unknown field: $name1")
        }
      }
    }
    reader.endObject()

    return UpbitOrderBook2(
      market = checkNotNull(_market) { "required field not set: market" },
      timestamp = checkNotNull(_timestamp) { "required field not set: timestamp" },
      totalAskSize = _totalAskSize,
      totalBidSize = checkNotNull(_totalBidSize) { "required field not set: totalBidSize" },
      orderbookUnits = checkNotNull(_orderbookUnits) { "required field not set: orderbookUnits" },
    )
  }

  override fun write(writer: JsonWriter, value: UpbitOrderBook2) {
    writer.beginObject()
    writer.name("cd")
    gson.toJson(value.market, UpbitMarket::class.java, writer)
    writer.name("tms")
    writer.value(value.timestamp)
    writer.name("tas")
    gson.toJson(value.totalAskSize, BigDecimal::class.java, writer)
    writer.name("tbs")
    gson.toJson(value.totalBidSize, BigDecimal::class.java, writer)
    writer.name("obu")
    writer.beginArray()
    value.orderbookUnits.forEach { elem3 ->
      OrderBookEntry2ShortFormAdapter(gson).write(writer, elem3)
    }
    writer.endArray()
    writer.endObject()
  }
}

class OrderBookEntry2ShortFormAdapter(val gson: Gson): TypeAdapter<OrderBookEntry2>() {
  override fun read(reader: JsonReader): OrderBookEntry2 {
    var _askPrice: BigDecimal? = null
    var set_askPrice: Boolean = false
    var _askSize: BigDecimal? = null
    var set_askSize: Boolean = false
    var _bidPrice: BigDecimal? = null
    var set_bidPrice: Boolean = false
    var _bidSize: BigDecimal? = null
    var set_bidSize: Boolean = false

    reader.beginObject()
    while (reader.hasNext()) {
      when (val name1 = reader.nextName()) {
        "ap" -> {
          check(!set_askPrice) { "Duplicate field: askPrice" }
          set_askPrice = true
          _askPrice = gson.fromJson(reader, BigDecimal::class.java)
        }

        "as" -> {
          check(!set_askSize) { "Duplicate field: askSize" }
          set_askSize = true
          _askSize = gson.fromJson(reader, BigDecimal::class.java)
        }

        "bp" -> {
          check(!set_bidPrice) { "Duplicate field: bidPrice" }
          set_bidPrice = true
          _bidPrice = gson.fromJson(reader, BigDecimal::class.java)
        }

        "bs" -> {
          check(!set_bidSize) { "Duplicate field: bidSize" }
          set_bidSize = true
          _bidSize = gson.fromJson(reader, BigDecimal::class.java)
        }

        else -> {
          throw IllegalStateException("Unknown field: $name1")
        }
      }
    }
    reader.endObject()

    return OrderBookEntry2(
      askPrice = checkNotNull(_askPrice) { "required field not set: askPrice" },
      askSize = checkNotNull(_askSize) { "required field not set: askSize" },
      bidPrice = checkNotNull(_bidPrice) { "required field not set: bidPrice" },
      bidSize = checkNotNull(_bidSize) { "required field not set: bidSize" },
    )
  }

  override fun write(writer: JsonWriter, value: OrderBookEntry2) {
    writer.beginObject()
    writer.name("ap")
    gson.toJson(value.askPrice, BigDecimal::class.java, writer)
    writer.name("as")
    gson.toJson(value.askSize, BigDecimal::class.java, writer)
    writer.name("bp")
    gson.toJson(value.bidPrice, BigDecimal::class.java, writer)
    writer.name("bs")
    gson.toJson(value.bidSize, BigDecimal::class.java, writer)
    writer.endObject()
  }
}
