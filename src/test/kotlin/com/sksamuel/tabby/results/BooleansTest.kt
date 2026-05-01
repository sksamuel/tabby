package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class BooleansTest : FunSpec() {
   init {

      test("failIfFalse fails when the value is false") {
         Result.success(false).failIfFalse().shouldBeFailure().shouldBeInstanceOf<NoSuchElementException>()
      }

      test("failIfFalse succeeds when the value is true") {
         Result.success(true).failIfFalse().shouldBeSuccess()
      }

      test("failIfFalse(fn) uses the supplied exception") {
         val expected = IllegalStateException("nope")
         Result.success(false).failIfFalse { expected }.exceptionOrNull() shouldBe expected
      }

      test("failIfTrue fails when the value is true") {
         Result.success(true).failIfTrue().shouldBeFailure().shouldBeInstanceOf<NoSuchElementException>()
      }

      test("failIfTrue succeeds when the value is false") {
         Result.success(false).failIfTrue().shouldBeSuccess()
      }

      test("failIfTrue(fn) uses the supplied exception") {
         val expected = IllegalStateException("nope")
         Result.success(true).failIfTrue { expected }.exceptionOrNull() shouldBe expected
      }

      test("failIfTrue(fn) does not invoke fn when the value is false") {
         var invoked = false
         Result.success(false).failIfTrue { invoked = true; RuntimeException() }.shouldBeSuccess()
         invoked shouldBe false
      }
   }
}
