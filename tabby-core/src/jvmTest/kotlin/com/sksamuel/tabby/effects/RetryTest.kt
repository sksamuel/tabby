package com.sksamuel.tabby.effects

import com.sksamuel.tabby.option.none
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeBetween
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.measureTime
import kotlin.time.milliseconds

class RetryTest : FunSpec() {
   init {

      test("IO.retry should invoke up to Schedule.iterations") {
         var iterations = 0
         IO.effect {
            iterations++
            error("boom")
         }.retry(Schedule.iterations(5)).run().getErrorUnsafe().message shouldBe "boom"
         iterations shouldBe 6
      }

      test("IO.retry should return once successful") {
         var iterations = 0
         IO.effect {
            iterations++
            if (iterations == 5) "foo" else error("boom")
         }.retry(Schedule.forever()).run().getValueUnsafe() shouldBe "foo"
         iterations shouldBe 5
      }

      test("IO.retry should not use schedule if successful") {
         val triggered = AtomicBoolean(false)
         IO { "foo" }.retry {
            triggered.set(true)
            Decision.Continue(none, Schedule.forever())
         }.run()
         triggered.get() shouldBe false
      }

      test("IO.retry with delayed schedule") {
         measureTime {
            IO { error("boom") }.retry(Schedule.iterations(3).delay(100.milliseconds)).run()
         }.toLongMilliseconds().shouldBeBetween(300, 400)
      }
   }
}
