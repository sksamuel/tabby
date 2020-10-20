package com.sksamuel.tabby.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ScheduleNTest : FunSpec() {
   init {

      test("Schedule.once should run the effect twice, if the initial is successful") {
         var counter = 0
         val effect = IO.effect { counter++ }
         effect.repeat(Schedule.once()).run()
         counter shouldBe 2
      }

      test("Schedule.once should run the effect once, if the initial run failed") {
         var counter = 0
         val effect = IO.effect {
            counter++
            error("Boom") // failure here means no repeat
         }
         effect.repeat(Schedule.once()).run()
         counter shouldBe 1
      }

      test("Schedule.iterations should run the effect the given number of times") {
         var counter = 0
         val effect = IO.effect { counter++ }
         effect.repeat(Schedule.iterations(8)).run()
         counter shouldBe 9
      }
   }
}
