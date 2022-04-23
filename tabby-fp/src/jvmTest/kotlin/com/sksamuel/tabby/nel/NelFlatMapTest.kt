package com.sksamuel.tabby.nel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NelFlatMapTest : FunSpec() {
   init {
      test("flatMap nel") {
         NonEmptyList("foo", "bar").flatMap { NonEmptyList(1, 2) } shouldBe NonEmptyList(1, 2, 1, 2)
      }
   }
}
