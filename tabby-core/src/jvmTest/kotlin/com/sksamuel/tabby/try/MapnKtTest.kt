package com.sksamuel.tabby.`try`

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MapnKtTest : FunSpec({

   test("mapN arity 2") {
      Try.mapN(Try.success("a"), Try.success("b")) { a, b -> a + b }.getValueUnsafe() shouldBe "ab"
      Try.mapN(Try.success("a"), Try.failure("boom")) { a, b -> a + b }.getErrorUnsafe().message shouldBe "boom"
   }

   test("mapN arity 3") {
      Try.mapN(Try.success("a"), Try.success("b"), Try.success("c")) { a, b, c -> a + b + c }
         .getValueUnsafe() shouldBe "abc"
      Try.mapN(Try.success("a"), Try.success("b"), Try.failure("boom")) { a, b, c -> a + b + c }
         .getErrorUnsafe().message shouldBe "boom"
   }

   test("mapN arity 4") {
      Try.mapN(Try.success("a"), Try.success("b"), Try.success("c"), Try.success("d")) { a, b, c, d -> a + b + c + d }
         .getValueUnsafe() shouldBe "abcd"
      Try.mapN(
         Try.success("a"),
         Try.success("b"),
         Try.failure("boom"),
         Try.success("d")
      ) { a, b, c, d -> a + b + c + d }
         .getErrorUnsafe().message shouldBe "boom"
   }
})
