package com.giyeok.jsontype

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.jupiter.api.Test

class Generated2Test {
  inline fun <reified T> test(gson: Gson, obj: T) {
    val json = gson.toJson(obj)
    println(json)
    val ds = gson.fromJson(json, T::class.java)
    println(ds)
    assertThat(obj).isEqualTo(ds)
  }

  @Test
  fun test() {
    val gson = GsonBuilder()
      .registerTypeAdapterFactory(ObjRestTest1Adapter.FACTORY)
      .registerTypeAdapterFactory(ObjMapTestAdapter.FACTORY)
      .registerTypeAdapterFactory(ArrayRestTest1Adapter.FACTORY)
      .registerTypeAdapterFactory(ArrayTestAdapter.FACTORY)
      .create()

    test(gson, ObjRestTest1(mapOf("abc" to "hello")))
    test(gson, ObjMapTest(mapOf("abc" to "xyz")))
    test(gson, ArrayRestTest1(listOf("abc", "xyz")))
    test(gson, ArrayTest(array = listOf("abc", "xyz")))
  }
}
