package com.sksamuel.tabby.effects

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlin.time.milliseconds

class ParTest : FunSpec() {
   init {

      test("IO.par should run in parallel with suspend").config(timeout = 350.milliseconds) {
         val a = IO {
            delay(250)
            "foo"
         }
         val b = IO {
            delay(250)
            "bar"
         }
         IO.par(a, b).runUnsafe() shouldBe listOf("foo", "bar")
      }

      test("IO.par extension version should run in parallel with suspend").config(timeout = 350.milliseconds) {
         val a = IO {
            delay(250)
            "foo"
         }
         val b = IO {
            delay(250)
            "bar"
         }
         listOf(a, b).par().runUnsafe() shouldBe listOf("foo", "bar")
      }

      test("IO.par should run in parallel with cpu").config(timeout = 1400.milliseconds) {
         val a = IO {
            val end = System.currentTimeMillis() + 1000
            while (System.currentTimeMillis() < end) {
            }
            "foo"
         }
         IO.par(a, a).onContext(Dispatchers.IO).runUnsafe() shouldBe listOf("foo", "foo")
      }

      test("IO.par should run in parallel with blocking").config(timeout = 1400.milliseconds) {
         val a = IO {
            Thread.sleep(1000)
            "foo"
         }
         IO.par(a, a).onContext(Dispatchers.IO).runUnsafe() shouldBe listOf("foo", "foo")
      }

      test("a failure should short circuit IO.par").config(timeout = 1000.milliseconds) {
         val a = IO {
            delay(100)
            error("boom")
         }
         val b = IO {
            delay(10000)
            "foo"
         }
         IO.par(a, b).run().getErrorUnsafe().message shouldBe "boom"
      }
   }
}
