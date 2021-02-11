package com.sksamuel.tabby.effects

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class ScheduleWhileTest : FunSpec() {
   init {

      test("Schedule.whileTrue should run while predicate is true") {
         val counter = AtomicInteger(0)
         val effect = IO { counter.incrementAndGet() }
         effect.repeat(Schedule.whileTrue { counter.get() < 10 }).run()
         counter.get() shouldBe 10
      }

      test("Schedule.whileTrue should run while predicate is true or an error is reached") {
         var counter = 0
         val effect = IO.effect {
            counter++
            if (counter == 5) error("boom")
            counter
         }
         effect.repeat(Schedule.whileTrue { counter < 10 }).run()
         counter shouldBe 5
      }
   }
}
