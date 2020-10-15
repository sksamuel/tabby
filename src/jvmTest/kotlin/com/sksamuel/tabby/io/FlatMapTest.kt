package com.sksamuel.tabby.io

import com.sksamuel.tabby.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FlatMapTest : FunSpec() {
   init {
      test("flatmap should invoke combinator on success") {
         IO.effect { "foo" }.flatMap { IO.effect { it.length } }.run() shouldBe 3.right()
      }
      test("flatmap should retain error") {
         IO.effect { error("boom") }.flatMap { IO.effect { "hello" } }.run().getLeftUnsafe().message shouldBe "boom"
      }
   }
}
