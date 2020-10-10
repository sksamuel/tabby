package com.sksamuel.tabby

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RefineOrDieTest : FunSpec() {
   init {
      test("refining should accept valid instance") {
         IO.failure("foo").refineOrDie<String>().run() shouldBe "foo".left()
      }
      test("refining should throw on invalid instance") {
         shouldThrowAny {
            IO.failure(123).refineOrDie<String>().run()
         }
      }
   }
}
