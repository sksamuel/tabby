package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.success
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class MapNTest : FunSpec() {
   init {
      test("IO.mapN should invoke combinator on two successful inputs") {
         IO.pure("foo").mapN(IO.pure("bar")) { a, b -> a + b }.run() shouldBe "foobar".success()
      }
      test("IO.mapN should fail if either io is a failure") {
         IO.pure("foo").mapN(IO.effect<String> { error("bar") }) { a, b -> a + b }.run().shouldBeInstanceOf<Try.Failure>()
         IO.effect<String> { error("bar") }.mapN(IO.pure("foo")) { a, b -> a + b }.run().shouldBeInstanceOf<Try.Failure>()
      }
   }
}
