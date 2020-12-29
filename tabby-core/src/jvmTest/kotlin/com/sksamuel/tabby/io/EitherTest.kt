package com.sksamuel.tabby.io

import com.sksamuel.tabby.either.left
import com.sksamuel.tabby.either.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EitherTest : FunSpec() {
   init {
      test("Either.effect should wrap a right in an IO.success") {
         "hello".right().effect().flatMap { IO.success { it.length } }.run().getRightUnsafe() shouldBe 5
         "hello".right().effect().flatMap { IO.success("foo") }.run().getRightUnsafe() shouldBe "foo"
      }

      test("Either.effect should wrap a left in an IO.failure") {
         "boom".left().effect().flatMap { IO.success { "foo" } }.run().getLeftUnsafe() shouldBe "boom"
      }
   }
}
