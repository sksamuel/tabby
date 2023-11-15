package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class OnSuccessTest : FunSpec() {
   init {

      test("onSuccessIfNotNull with non null") {
         val name: String? = "sam"
         var fired = false
         Result.success(name).onSuccessIfNotNull { fired = true }
         fired.shouldBeTrue()
      }

      test("onSuccessIfNotNull with null") {
         val name: String? = null
         var fired = false
         Result.success(name).onSuccessIfNotNull { fired = true }
         fired.shouldBeFalse()
      }

      test("onSuccessIfNull with non null") {
         val name: String? = "sam"
         var fired = false
         Result.success(name).onSuccessIfNull { fired = true }
         fired.shouldBeFalse()
      }

      test("onSuccessIfNull with null") {
         val name: String? = null
         var fired = false
         Result.success(name).onSuccessIfNull { fired = true }
         fired.shouldBeTrue()
      }

      test("onSuccessIfTrue with failure") {
         var fired = false
         Result.failure<Boolean>(Exception("")).onSuccessIfTrue { fired = true }
         fired.shouldBeFalse()
      }

      test("onSuccessIfTrue with true") {
         val bool = true
         var fired = false
         Result.success(bool).onSuccessIfTrue { fired = true }
         fired.shouldBeTrue()
      }

      test("onSuccessIfTrue with false") {
         val bool = false
         var fired = false
         Result.success(bool).onSuccessIfTrue { fired = true }
         fired.shouldBeFalse()
      }

      test("onSuccessIfFalse with true") {
         val bool = true
         var fired = false
         Result.success(bool).onSuccessIfFalse { fired = true }
         fired.shouldBeFalse()
      }

      test("onSuccessIfFalse with false") {
         val bool = false
         var fired = false
         Result.success(bool).onSuccessIfFalse { fired = true }
         fired.shouldBeTrue()
      }

      test("onSuccessIf with delegate") {
         val inputParam = "test-string"
         val delegate: (String) -> Boolean = { true }
         var seenValue: String? = null
         Result.success(inputParam).onSuccessIf(delegate) { seenValue = it }
         seenValue shouldBe inputParam
      }

      test("onSuccessIfNotNull with null") {
         val inputParam: String = "test-string"
         val delegate: (String?) -> Boolean = { false }
         var seenValue: String? = null
         Result.success(inputParam).onSuccessIfNotNull(delegate) { seenValue = it }
         seenValue.shouldBeNull()
      }

      test("onSuccessIfNotNull with not null and false") {
         val inputParam = "test-string"
         val delegate: (String) -> Boolean = { false }
         var seenValue: String? = null
         Result.success(inputParam).onSuccessIfNotNull(delegate) { seenValue = it }
         seenValue.shouldBeNull()
      }

      test("onSuccessIfNotNull with not null and true") {
         val inputParam = "test-string"
         val delegate: (String) -> Boolean = { true }
         var seenValue: String? = null
         Result.success(inputParam).onSuccessIfNotNull(delegate) { seenValue = it }
         seenValue shouldBe inputParam
      }
   }
}
