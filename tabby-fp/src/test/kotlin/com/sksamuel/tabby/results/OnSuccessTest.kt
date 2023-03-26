package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

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
   }
}
