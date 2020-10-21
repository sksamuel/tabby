package com.sksamuel.tabby.io

import com.sksamuel.tabby.Either
import com.sksamuel.tabby.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class MapNTest : FunSpec() {
   init {
      test("zip should invoke combinator on two successful inputs") {
         IO.success("foo").mapN(IO.success("bar")) { a, b -> a + b }.run() shouldBe "foobar".right()
      }
      test("zip should fail if either io is a failure") {
         IO.success("foo").mapN(IO.effect<String> { error("bar") }) { a, b -> a + b }.run().shouldBeInstanceOf<Either.Left<*>>()
         IO.effect<String> { error("bar") }.mapN(IO.success("foo")) { a, b -> a + b }.run().shouldBeInstanceOf<Either.Left<*>>()
      }
   }
}
