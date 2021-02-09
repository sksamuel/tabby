package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.success
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FlatMapTest : FunSpec() {
   init {
      test("flatmap should invoke combinator on success") {
         IO { "foo" }.flatMap { IO { it.length } }.run() shouldBe 3.success()
         IO { "foo" }.flatMap { IO.pure("foo") }.run() shouldBe "foo".success()
         IO { "foo" }.flatMap { IO.failure("boom") }.run().getErrorUnsafe().message shouldBe "boom"
      }
      test("flatmap should retain error") {
         IO.effect { error("boom") }.flatMap { IO.effect { "hello" } }.run().getErrorUnsafe().message shouldBe "boom"
      }
   }
}
