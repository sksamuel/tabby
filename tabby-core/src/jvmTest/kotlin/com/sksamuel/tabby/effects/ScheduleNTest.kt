package com.sksamuel.tabby.effects

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeBetween
import io.kotest.matchers.shouldBe
import kotlin.time.milliseconds

class ScheduleNTest : FunSpec() {
   init {

      test("Schedule.iterations should run the effect the given number of times") {
         var counter = 0
         val effect = IO.effect { counter++ }
         effect.repeat(Schedule.iterations(8)).run()
         counter shouldBe 9
      }

      test("Schedule.iterations with delay") {
         var counter = 0
         val start = System.currentTimeMillis()
         val effect = IO.effect { counter++ }
         effect.repeat(Schedule.iterations(5).delay(75.milliseconds)).run()
         counter shouldBe 6
         val duration = System.currentTimeMillis() - start
         duration.shouldBeBetween(375, 475)
      }
   }
}
