package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.shouldBe

class MapIfTest : FunSpec() {
   init {

      test("mapIf should invoke if the predicate is true") {
         Result.success("foo").mapIf({ it == "foo" }) { "bar" }.getOrThrow() shouldBe "bar"
      }

      test("mapIf should not invoke if the predicate is false") {
         Result.success("foo").mapIf({ it == "foo" }) { "bar" }.getOrThrow() shouldBe "bar"
      }

      test("mapIf should not invoke for a failure") {
         Result.failure<String>(IndexOutOfBoundsException("foo")).mapIf({ true }) { "bar" }.shouldBeFailure()
      }
   }
}
