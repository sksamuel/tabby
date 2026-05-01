package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FailIfTest : FunSpec() {
   init {

      test("failIf(p) fails with RuntimeException(\"failure\") when predicate is true") {
         Result.success(5).failIf { it > 0 }.shouldBeFailure()
            .shouldBeInstanceOf<RuntimeException>()
            .message shouldBe "failure"
      }

      test("failIf(p) succeeds unchanged when predicate is false") {
         Result.success(5).failIf { it < 0 }.shouldBeSuccess() shouldBe 5
      }

      test("failIf(message, p) fails with the given message when predicate is true") {
         Result.success(5).failIf("nope") { it > 0 }.shouldBeFailure()
            .message shouldBe "nope"
      }

      test("failIf(message, p) succeeds unchanged when predicate is false") {
         Result.success(5).failIf("nope") { it < 0 }.shouldBeSuccess() shouldBe 5
      }

      test("failIf(exceptionFn, p) uses the supplied lambda's exception") {
         val ex = IllegalStateException("custom")
         Result.success(5).failIf({ ex }, { it > 0 }).exceptionOrNull() shouldBe ex
      }

      test("failIf(exceptionFn, p) does NOT invoke the lambda when predicate is false") {
         var invoked = 0
         Result.success(5).failIf({ invoked++; RuntimeException() }, { it < 0 })
         invoked shouldBe 0
      }

      test("failIf propagates the original failure unchanged") {
         val original = RuntimeException("original")
         Result.failure<Int>(original).failIf { true }.exceptionOrNull() shouldBe original
      }
   }
}
