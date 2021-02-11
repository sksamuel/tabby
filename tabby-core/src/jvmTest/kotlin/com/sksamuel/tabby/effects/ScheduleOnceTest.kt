package com.sksamuel.tabby.effects

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ScheduleOnceTest : FunSpec() {
   init {

      test("schedule once should run the effect twice, if the initial is successful") {
         var counter = 0
         val effect = IO { counter++ }
         effect.repeat(Schedule.once).run()
         counter shouldBe 2
      }

      test("schedule once should run the effect once, if the initial run failed") {
         var counter = 0
         val effect = IO {
            counter++
            error("Boom") // failure here means no repeat
         }
         effect.repeat(Schedule.once).run()
         counter shouldBe 1
      }
   }
}
