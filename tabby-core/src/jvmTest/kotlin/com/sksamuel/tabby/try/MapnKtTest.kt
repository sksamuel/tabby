package com.sksamuel.tabby.`try`

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MapnKtTest : FunSpec({

   test("mapN arity 2") {
      Try.mapN(Try.success("a"), Try.success("b")) { a, b -> a + b }.getValueUnsafe() shouldBe "ab"
   }
})
