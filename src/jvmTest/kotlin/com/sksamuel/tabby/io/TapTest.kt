package com.sksamuel.tabby.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import java.util.concurrent.atomic.AtomicBoolean

class TapTest : FunSpec() {
   init {

      test("tap should run the side effect for successes") {
         val atomic = AtomicBoolean(false)
         IO.success("foo").tap { IO.effect { atomic.set(true) } }.run()
         atomic.get().shouldBeTrue()
      }

      test("tap should not run the side effect for failures") {
         val atomic = AtomicBoolean(false)
         IO.failure("foo").tap { IO.effect { atomic.set(true) } }.run()
         atomic.get().shouldBeFalse()
      }

      test("tap error should not run the side effect for successes") {
         val atomic = AtomicBoolean(false)
         IO.success("foo").tapError { IO.effect { atomic.set(true) } }.run()
         atomic.get().shouldBeFalse()
      }

      test("tap error should not run the side effect for failures") {
         val atomic = AtomicBoolean(false)
         IO.failure("foo").tapError { IO.effect { atomic.set(true) } }.run()
         atomic.get().shouldBeTrue()
      }
   }
}
