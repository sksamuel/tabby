package com.sksamuel.tabby.effects

import com.sksamuel.tabby.results.failure
import com.sksamuel.tabby.results.success
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class ScheduleWhileTest : FunSpec() {
   init {

      test("Schedule.whileTrue should run while predicate is true") {
         val counter = AtomicInteger(0)
         repeat(Schedule.whileTrue { counter.get() < 10 }) {
            counter.incrementAndGet()
            counter.success()
         }
         counter.get() shouldBe 10
      }

      test("Schedule.whileTrue should run while predicate is true or an error is reached") {
         var counter = 0
         repeat(Schedule.whileTrue { counter < 10 }) {
            counter++
            if (counter == 5)
               "boom".failure()
            else
               counter.success()
         }
         counter shouldBe 5
      }
   }
}
