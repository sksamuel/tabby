package com.sksamuel.tabby.nel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NelMapTest : FunSpec() {
   init {
      test("map non empty list") {
         NonEmptyList("foo", "bar").map { it.length } shouldBe NonEmptyList(3, 3)
      }
   }
}
