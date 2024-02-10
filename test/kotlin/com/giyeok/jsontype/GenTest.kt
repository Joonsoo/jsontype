package com.giyeok.jsontype

import com.google.gson.GsonBuilder
import org.junit.jupiter.api.Test
import java.math.BigDecimal
//
//class GenTest {
//  @Test
//  fun test() {
//    val gson = GsonBuilder()
//      .registerTypeAdapterFactory(UpbitOrderBook2Adapter.FACTORY)
//      .create()
//
//    val d = UpbitOrderBook2(
//      UpbitMarket.BTCKRW,
//      100L,
//      null,
//      BigDecimal(123),
//      listOf(
//        OrderBookEntry2(BigDecimal(100), BigDecimal(200), BigDecimal(300), BigDecimal(400)),
//        OrderBookEntry2(BigDecimal(100), BigDecimal(200), BigDecimal(300), BigDecimal(400))
//      )
//    )
//
//    val ser = gson.toJson(d)
//    println(ser)
//    val parsed = gson.fromJson(ser, UpbitOrderBook2::class.java)
//    println(parsed)
//  }
//}
