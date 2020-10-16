package com.sksamuel.tabby.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeBetween
import io.kotest.matchers.shouldBe
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
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

      test("schedule forever should run until an error with added delay") {
         var counter = 0
         val start = System.currentTimeMillis()
         val effect = IO.effect {
            counter++
            if (counter == 10)
               error("stop")
         }
         effect.repeat(Schedule.Forever.delay(100.milliseconds)).run()
         val duration = System.currentTimeMillis() - start
         counter shouldBe 10
         duration.shouldBeBetween(750, 1250)
      }
   }
}
