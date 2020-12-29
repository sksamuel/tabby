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
         effect.repeat(Schedule.forever()).run()
         counter shouldBe 1000
      }

      test("Schedule.delay should run until an error with added delay") {
         var counter = 0
         val start = System.currentTimeMillis()
         val effect = IO.effect {
            counter++
            if (counter == 10)
               error("stop")
         }
         effect.repeat(Schedule.delay(100.milliseconds)).run()
         val duration = System.currentTimeMillis() - start
         counter shouldBe 10
         duration.shouldBeBetween(750, 1250)
      }

      test("Schedule.delay should run until an error with added delay from function") {
         var counter = 0
         val start = System.currentTimeMillis()
         val effect = IO.effect {
            counter++
            if (counter == 10)
               error("stop")
            counter
         }
         effect.repeat(Schedule.delay { (it * 25).milliseconds }).run()
         val duration = System.currentTimeMillis() - start
         counter shouldBe 10
         duration.shouldBeBetween(925, 1325)
      }

      test("schedule forever should run until an error with delay if") {
         var counter = 0
         val start = System.currentTimeMillis()
         val effect = IO.effect {
            counter++
            if (counter == 10)
               error("stop")
            counter
         }
         effect.repeat(Schedule.delayIf(100.milliseconds) { it < 3 }).run()
         val duration = System.currentTimeMillis() - start
         counter shouldBe 10
         duration.shouldBeBetween(100, 350)
      }
   }
}
