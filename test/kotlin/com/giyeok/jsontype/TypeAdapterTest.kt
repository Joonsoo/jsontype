package com.giyeok.jsontype

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.junit.jupiter.api.Test
import java.io.StringReader
import java.math.BigDecimal

class TypeAdapterTest {
  @Test
  fun test3() {
    val gson = GsonBuilder().create()
    val x = gson.fromJson("123", BigDecimal::class.java)
    println(x)
  }

  @Test
  fun test() {
    val gson1 = GsonBuilder()
      .create()
    val json = "{\"result\": \"hello\", \"errorCode\": \"0\"}"
    val result = gson1.fromJson(json, CoinoneOrderBook::class.java)
    println(result)

    val gson2 = GsonBuilder()
      .registerTypeAdapterFactory(CoinoneOrderBookAdapter.FACTORY)
      .create()
    val rr1 = CoinoneOrderBookAdapter(gson2)
      .read(gson2.newJsonReader(StringReader(json)))
    println(rr1)

    println()
  }

  @Test
  fun test2() {
    val gson = GsonBuilder().create()
    val json = """
      {"obu": [
      {"ap": 1, "aq": 2}
      ]}
    """.trimIndent()
    val result = UpbitOrderBookLongFormAdapter(gson).read(gson.newJsonReader(StringReader(json)))
    println(result)
  }
}

class CoinoneOrderBookAdapter(val gson: Gson): TypeAdapter<CoinoneOrderBook>() {
  companion object {
    val FACTORY = object: TypeAdapterFactory {
      override fun <T: Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType == CoinoneOrderBook::class.java) {
          return CoinoneOrderBookAdapter(gson) as TypeAdapter<T>
        }
        return null
      }
    }
  }

  override fun read(reader: JsonReader): CoinoneOrderBook {
    var result: String? = null
    var errorCode: String? = null
    var askPrice: BigDecimal? = null

    reader.beginObject()
    while (reader.hasNext()) {
      val name = reader.nextName()
      when (name) {
        "result" -> {
          check(result == null) { "Duplicate field: result" }
          result = gson.fromJson(reader, String::class.java)
        }

        "errorCode" -> {
          check(errorCode == null) { "Duplicate field: errorCode" }
          errorCode = gson.fromJson(reader, String::class.java)
        }

        "ask_price", "ap" -> {
          check(askPrice == null) { "Duplicate field: askPrice" }
          askPrice = gson.fromJson(reader, BigDecimal::class.java)
        }
      }
    }
    reader.endObject()
    return CoinoneOrderBook(
      checkNotNull(result) { "`result` field is not set" },
      checkNotNull(errorCode) { "`errorCode` field is not set" },
      checkNotNull(askPrice) { "`askPrice` field is not set" },
    )
  }

  override fun write(writer: JsonWriter, value: CoinoneOrderBook) {
    gson.toJson(value, CoinoneOrderBook::class.java, writer)
  }
}

data class CoinoneOrderBook(
  val result: String,
  val errorCode: String,
  val askPrice: BigDecimal,
)

data class UpbitOrderBook(
  val orderbookUnits: List<OrderBookEntry>
)

data class OrderBookEntry(
  val askPrice: BigDecimal,
  val askQty: BigDecimal,
)

class UpbitOrderBookLongFormAdapter(val gson: Gson): TypeAdapter<UpbitOrderBook>() {
  companion object {
    val FACTORY = object: TypeAdapterFactory {
      override fun <T: Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType == UpbitOrderBook::class.java) {
          return UpbitOrderBookLongFormAdapter(gson) as TypeAdapter<T>
        }
        return null
      }
    }
  }

  override fun read(reader: JsonReader): UpbitOrderBook {
    var orderbookUnits: MutableList<OrderBookEntry>? = null

    reader.beginObject()
    while (reader.hasNext()) {
      val name = reader.nextName()
      when (name) {
        "orderbook_units", "obu" -> {
          check(orderbookUnits == null) { "Duplicate field: orderbookUnits" }
          reader.beginArray()
          orderbookUnits = mutableListOf()
          val elemReader = OrderBookEntryAdapter(gson)
          while (reader.hasNext()) {
            orderbookUnits.add(elemReader.read(reader))
          }
          reader.endArray()
        }
      }
    }
    reader.endObject()

    return UpbitOrderBook(
      orderbookUnits = checkNotNull(orderbookUnits).toList()
    )
  }

  override fun write(writer: JsonWriter, value: UpbitOrderBook) {
    TODO("Not yet implemented")
  }
}

class OrderBookEntryAdapter(val gson: Gson): TypeAdapter<OrderBookEntry>() {
  override fun read(reader: JsonReader): OrderBookEntry {
    var ap: BigDecimal? = null
    var aq: BigDecimal? = null

    reader.beginObject()
    while (reader.hasNext()) {
      val name = reader.nextName()
      when (name) {
        "ap" -> {
          ap = gson.fromJson(reader, BigDecimal::class.java)
        }

        "aq" -> {
          aq = gson.fromJson(reader, BigDecimal::class.java)
        }
      }
    }
    reader.endObject()

    return OrderBookEntry(
      checkNotNull(ap) { "" },
      checkNotNull(aq) { "" })
  }

  override fun write(writer: JsonWriter, value: OrderBookEntry) {
    TODO("Not yet implemented")
  }
}
