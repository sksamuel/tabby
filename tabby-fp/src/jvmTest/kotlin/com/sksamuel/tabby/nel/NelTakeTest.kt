package com.sksamuel.tabby.nel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NelTakeTest : FunSpec() {
   init {
      test("NonEmptyList.take") {
         NonEmptyList("foo", "bar").take(2) shouldBe listOf("foo", "bar")
         NonEmptyList("foo", "bar").take(1) shouldBe listOf("foo")
         NonEmptyList("foo", "bar").take(0) shouldBe emptyList()
      }
   }
}
