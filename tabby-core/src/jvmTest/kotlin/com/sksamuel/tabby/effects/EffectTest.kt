package com.sksamuel.tabby.effects

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

class EffectTest : FunSpec() {
   init {
      test("effect happy path") {
         val a = IO { "a" }
         val b = IO {
            delay(100)
            "b"
         }

         val c = effect {
            a.await() + b.await()
         }

         c.runUnsafe() shouldBe "ab"
      }

      test("effect short circuit on error") {
         val a = IO {
            if (System.currentTimeMillis() > 0) error("foo") else "a"
         }
         val b = IO {
            delay(100)
            "b"
         }

         val c = effect {
            a.await() + b.await()
         }

         c.run().getErrorUnsafe().message shouldBe "foo"
      }
   }
}
