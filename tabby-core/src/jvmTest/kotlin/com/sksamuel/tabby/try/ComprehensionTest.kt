package com.sksamuel.tabby.`try`

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ComprehensionTest : FunSpec() {
   init {

      test("try comprehension without errors") {

         val a = Try { "a" }
         val b = Try { "b" }

         val c = forcomp {
            !a + !b
         }


         c shouldBe Try.success("ab")
      }

      test("try short circuit") {

         val a = Try<String> { error("a") }
         val b = Try { "b" }

         val c = forcomp {
            !a + !b
         }

         c.getErrorUnsafe().message shouldBe "a"
      }
   }
}
