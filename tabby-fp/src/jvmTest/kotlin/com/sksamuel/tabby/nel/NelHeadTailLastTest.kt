package com.sksamuel.tabby.nel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NelHeadTailLastTest : FunSpec() {
   init {
      test("NonEmptyList.head") {
         NonEmptyList("foo", "bar").head shouldBe "foo"
         NonEmptyList("foo").head shouldBe "foo"
      }

      test("NonEmptyList.last") {
         NonEmptyList("foo", "bar").last shouldBe "bar"
         NonEmptyList("foo").last shouldBe "foo"
      }

      test("NonEmptyList.tail") {
         NonEmptyList("foo", "bar").tail shouldBe listOf("bar")
         NonEmptyList("foo").tail shouldBe emptyList()
      }
   }
}
