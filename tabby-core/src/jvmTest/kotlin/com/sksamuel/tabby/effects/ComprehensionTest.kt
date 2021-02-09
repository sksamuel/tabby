package com.sksamuel.tabby.effects

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ComprehensionTest : FunSpec() {
   init {

      test("try comprehension without errors") {

         val a = IO { "a" }
         val b = IO { "b" }

         val c = forcomp {
            !a + !b
         }

         c.runUnsafe() shouldBe "ab"
      }

      test("try short circuit") {

         val a = IO<String> { error("a") }
         val b = IO { "b" }

         val c = forcomp {
            !a + !b
         }

         c.run().getErrorUnsafe().message shouldBe "a"
      }
   }
}
