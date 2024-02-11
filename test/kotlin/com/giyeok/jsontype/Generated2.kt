package com.giyeok.jsontype

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

data class ObjRestTest1(
  val rest: Map<String, String>,
)

class ObjRestTest1Adapter(val gson: Gson): TypeAdapter<ObjRestTest1>() {
  companion object {
    val FACTORY = object: TypeAdapterFactory {
      override fun <T: Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType == ObjRestTest1::class.java) {
          return ObjRestTest1Adapter(gson) as TypeAdapter<T>
        }
        return null
      }
    }
  }

  override fun read(reader: JsonReader): ObjRestTest1 {
    val _rest = mutableMapOf<String, String>()
    reader.beginObject()
    while (reader.hasNext()) {
      when (val name1 = reader.nextName()) {
        else -> {
          check(name1 !in _rest) { "Duplicate field: name1" }
          _rest[name1] = reader.nextString()
        }
      }
    }
    reader.endObject()
    return ObjRestTest1(
      rest = _rest,
    )
  }

  override fun write(writer: JsonWriter, value: ObjRestTest1) {
    writer.beginObject()
    value.rest.entries.forEach { (key2, val2) ->
      writer.name(key2)
      writer.value(val2)
    }
    writer.endObject()
  }
}

data class ObjMapTest(
  val myObj: Map<String, String>,
)

class ObjMapTestAdapter(val gson: Gson): TypeAdapter<ObjMapTest>() {
  companion object {
    val FACTORY = object: TypeAdapterFactory {
      override fun <T: Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType == ObjMapTest::class.java) {
          return ObjMapTestAdapter(gson) as TypeAdapter<T>
        }
        return null
      }
    }
  }

  override fun read(reader: JsonReader): ObjMapTest {
    var _myObj: Map<String, String>? = null
    reader.beginObject()
    while (reader.hasNext()) {
      when (val name1 = reader.nextName()) {
        "my_obj" -> {
          check(_myObj == null) { "Duplicate field: myObj" }
          val map2 = mutableMapOf<String, String>()
          reader.beginObject()
          while (reader.hasNext()) {
            map2[reader.nextName()] = reader.nextString()
          }
          reader.endObject()
          _myObj = map2
        }

        else -> {
          throw IllegalStateException("Unknown field: $name1")
        }
      }
    }
    reader.endObject()
    return ObjMapTest(
      myObj = checkNotNull(_myObj) { "required field not set: myObj" },
    )
  }

  override fun write(writer: JsonWriter, value: ObjMapTest) {
    writer.beginObject()
    writer.name("my_obj")
    writer.beginObject()
    value.myObj.forEach { (key3, val3) ->
      writer.name(key3)
      writer.value(val3)
    }
    writer.endObject()
    writer.endObject()
  }
}

data class ArrayRestTest1(
  val rest: List<String>,
)

class ArrayRestTest1Adapter(val gson: Gson): TypeAdapter<ArrayRestTest1>() {
  companion object {
    val FACTORY = object: TypeAdapterFactory {
      override fun <T: Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType == ArrayRestTest1::class.java) {
          return ArrayRestTest1Adapter(gson) as TypeAdapter<T>
        }
        return null
      }
    }
  }

  override fun read(reader: JsonReader): ArrayRestTest1 {
    val _rest = mutableListOf<String>()
    reader.beginArray()
    while (reader.hasNext()) {
      _rest.add(reader.nextString())
    }
    reader.endArray()
    return ArrayRestTest1(
      rest = _rest,
    )
  }

  override fun write(writer: JsonWriter, value: ArrayRestTest1) {
    writer.beginArray()
    value.rest.forEach { elem1 ->
      writer.value(elem1)
    }
    writer.endArray()
  }
}

data class ArrayTest(
  val array: List<String>,
)

class ArrayTestAdapter(val gson: Gson): TypeAdapter<ArrayTest>() {
  companion object {
    val FACTORY = object: TypeAdapterFactory {
      override fun <T: Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType == ArrayTest::class.java) {
          return ArrayTestAdapter(gson) as TypeAdapter<T>
        }
        return null
      }
    }
  }

  override fun read(reader: JsonReader): ArrayTest {
    var _array: List<String>? = null
    reader.beginArray()
    check(_array == null) { "Duplicate field: array" }
    val array1 = mutableListOf<String>()
    reader.beginArray()
    while (reader.hasNext()) {
      array1.add(reader.nextString())
    }
    reader.endArray()
    _array = array1
    reader.endArray()
    return ArrayTest(
      array = checkNotNull(_array) { "required field not set: array" },
    )
  }

  override fun write(writer: JsonWriter, value: ArrayTest) {
    writer.beginArray()
    writer.beginArray()
    value.array.forEach { elem2 ->
      writer.value(elem2)
    }
    writer.endArray()
    writer.endArray()
  }
}
