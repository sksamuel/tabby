package com.sksamuel.tabby.`try`

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TryTest : FunSpec() {
   init {
      test("creating a try from a string error") {
         Try.failure("foo").getErrorUnsafe() shouldBe Exception("foo")
      }
   }
}
