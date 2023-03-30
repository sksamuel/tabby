package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.delay

class OnEachTest : FunSpec() {
   init {
      test("onEach should fire for failures") {
         var fired = false
         Result.failure<String>(NoSuchElementException()).onEach { fired = true }
         fired.shouldBeTrue()
      }
      test("onEach should fire for sucesses") {
         var fired = false
         Result.success("foo").onEach { fired = true }
         fired.shouldBeTrue()
      }
      test("onEach should be suspendable") {
         Result.unit().onEach { delay(10) }
      }
   }
}
