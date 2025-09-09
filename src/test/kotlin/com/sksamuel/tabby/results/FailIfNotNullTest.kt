package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess

class FailIfNotNullTest : FunSpec({

   test("failIfNotNull should fail if not null") {
      Result.success("foo").failIfNotNull().shouldBeFailure()
   }

   test("failIfNotNull(exception) should fail if not null") {
      Result.success("foo").failIfNotNull { IndexOutOfBoundsException() }.shouldBeFailure()
   }

   test("failIfNotNull should pass if null") {
      Result.success(null).failIfNotNull().shouldBeSuccess()
   }

   test("failIfNotNull(exception) should pass if null") {
      Result.success(null).failIfNotNull { IndexOutOfBoundsException() }.shouldBeSuccess()
   }
})
