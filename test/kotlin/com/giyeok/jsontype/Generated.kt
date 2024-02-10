package com.giyeok.jsontype

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

data class TestRest(
  val market: String,
  val sub1: String,
  val rest: Map<String, JsonElement>,
)

class TestRestAdapter(val gson: Gson): TypeAdapter<TestRest>() {
  companion object {
    val FACTORY = object: TypeAdapterFactory {
      override fun <T: Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType == TestRest::class.java) {
          return TestRestAdapter(gson) as TypeAdapter<T>
        }
        return null
      }
    }
  }

  override fun read(reader: JsonReader): TestRest {
    var _market: String? = null
    var _sub1: String? = null
    val _rest = mutableMapOf<String, JsonElement>()
    reader.beginObject()
    while (reader.hasNext()) {
      when (val name1 = reader.nextName()) {
        "market" -> {
          check(_market == null) { "Duplicate field: market" }
          _market = reader.nextString()
        }
        "sub" -> {
          reader.beginObject()
          while (reader.hasNext()) {
            when (val name2 = reader.nextName()) {
              "sub1" -> {
                check(_sub1 == null) { "Duplicate field: sub1" }
                _sub1 = reader.nextString()
              }
              else -> {
                throw IllegalStateException("Unknown field: $name2")
              }
            }
          }
          reader.endObject()
        }
        else -> {
          check(name1 !in _rest) { "Duplicate field: name1" }
          _rest[name1] = gson.fromJson(reader, JsonElement::class.java)
        }
      }
    }
    reader.endObject()
    return TestRest(
      market = checkNotNull(_market) { "required field not set: market" },
      sub1 = checkNotNull(_sub1) { "required field not set: sub1" },
      rest = _rest,
    )
  }

  override fun write(writer: JsonWriter, value: TestRest) {
    writer.beginObject()
    writer.name("market")
    writer.value(value.market)
    writer.name("sub")
    writer.beginObject()
    writer.name("sub1")
    writer.value(value.sub1)
    writer.endObject()
    value.rest.forEach { key3, value3 ->
      writer.name(key3)
      gson.toJson(value3, writer)
    }
    writer.endObject()
  }
}
