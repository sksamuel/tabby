package com.sksamuel.tabby.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ScheduleForeverTest : FunSpec() {
   init {

      test("schedule forever should run until an error") {
         var counter = 0
         val effect = IO.effect {
            counter++
            if (counter == 1000)
               error("stop")
         }
         effect.repeat(Schedule.Forever).run()
         counter shouldBe 1000
      }
   }
}
