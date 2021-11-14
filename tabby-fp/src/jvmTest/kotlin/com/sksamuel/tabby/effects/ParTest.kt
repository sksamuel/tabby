//package com.sksamuel.tabby.effects
//
//import com.sksamuel.tabby.Tuple3
//import com.sksamuel.tabby.Tuple4
//import com.sksamuel.tabby.`try`.success
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.shouldBe
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlin.time.milliseconds
//
//class ParTest : FunSpec() {
//   init {
//
//      test("parN(2) should run in parallel").config(timeout = 350.milliseconds) {
//         val a: Effect<String> = {
//            delay(250)
//            "foo".success()
//         }
//         val b: Effect<String> = {
//            delay(250)
//            "bar".success()
//         }
//         IO.parN(a, b).getValueUnsafe() shouldBe Pair("foo", "bar")
//      }
//
//      test("parN(2) should short circuit").config(timeout = 500.milliseconds) {
//         val a: Effect<String> = {
//            delay(100)
//            error("boom")
//         }
//         val b: Effect<String> = {
//            delay(1000)
//            "bar".success()
//         }
//         IO.parN(a, b).getErrorUnsafe().message shouldBe "boom"
//      }
//
//      test("parN(3) should run in parallel").config(timeout = 350.milliseconds) {
//         val a: Effect<String> = {
//            delay(250)
//            "foo".success()
//         }
//         val b: Effect<String> = {
//            delay(250)
//            "bar".success()
//         }
//         val c: Effect<String> = {
//            delay(250)
//            "baz".success()
//         }
//         IO.parN(a, b, c).getValueUnsafe() shouldBe Tuple3("foo", "bar", "baz")
//      }
//
//      test("parN(3) should short circuit").config(timeout = 500.milliseconds) {
//         val a: Effect<String> = {
//            delay(100)
//            error("boom")
//         }
//         val b: Effect<String> = {
//            delay(1000)
//            "bar".success()
//         }
//         val c: Effect<String> = {
//            delay(1000)
//            "baz".success()
//         }
//         IO.parN(a, b, c).getErrorUnsafe().message shouldBe "boom"
//      }
//
//      test("parN(4) should run in parallel").config(timeout = 350.milliseconds) {
//         val a: Effect<String> = {
//            delay(250)
//            "foo".success()
//         }
//         val b: Effect<String> = {
//            delay(250)
//            "bar".success()
//         }
//         val c: Effect<String> = {
//            delay(250)
//            "baz".success()
//         }
//         val d: Effect<String> = {
//            delay(250)
//            "woo".success()
//         }
//         IO.parN(a, b, c, d).getValueUnsafe() shouldBe Tuple4("foo", "bar", "baz", "woo")
//      }
//
//      test("parN(4) should short circuit").config(timeout = 500.milliseconds) {
//         val a: Effect<String> = {
//            delay(100)
//            error("boom")
//         }
//         val b: Effect<String> = {
//            delay(1000)
//            "bar".success()
//         }
//         val c: Effect<String> = {
//            delay(1000)
//            "baz".success()
//         }
//         val d: Effect<String> = {
//            delay(1000)
//            "woo".success()
//         }
//         IO.parN(a, b, c, d).getErrorUnsafe().message shouldBe "boom"
//      }
//
//      test("IO.par should run in parallel with suspend").config(timeout = 350.milliseconds) {
//         val a = IO {
//            delay(250)
//            "foo"
//         }
//         val b = IO {
//            delay(250)
//            "bar"
//         }
//         IO.par(a, b).runUnsafe() shouldBe listOf("foo", "bar")
//      }
//
//      test("IO.par extension version should run in parallel with suspend").config(timeout = 350.milliseconds) {
//         val a = IO {
//            delay(250)
//            "foo"
//         }
//         val b = IO {
//            delay(250)
//            "bar"
//         }
//         listOf(a, b).par().runUnsafe() shouldBe listOf("foo", "bar")
//      }
//
//      test("IO.par should run in parallel with cpu").config(timeout = 1400.milliseconds) {
//         val a = IO {
//            val end = System.currentTimeMillis() + 1000
//            while (System.currentTimeMillis() < end) {
//            }
//            "foo"
//         }
//         IO.par(a, a).onContext(Dispatchers.IO).runUnsafe() shouldBe listOf("foo", "foo")
//      }
//
//      test("IO.par should run in parallel with blocking").config(timeout = 1400.milliseconds) {
//         val a = IO {
//            Thread.sleep(1000)
//            "foo"
//         }
//         IO.par(a, a).onContext(Dispatchers.IO).runUnsafe() shouldBe listOf("foo", "foo")
//      }
//
//      test("a failure should short circuit IO.par").config(timeout = 1000.milliseconds) {
//         val a = IO {
//            delay(100)
//            error("boom")
//         }
//         val b = IO {
//            delay(10000)
//            "foo"
//         }
//         IO.par(a, b).run().getErrorUnsafe().message shouldBe "boom"
//      }
//   }
//}
