package com.sksamuel.tabby.io

import com.sksamuel.tabby.effects.IO
import com.sksamuel.tabby.effects.Schedule
import com.sksamuel.tabby.effects.repeat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeBetween
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.milliseconds

class ScheduleForeverTest : FunSpec() {
   init {

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
            if (counter == 10)
               error("stop")
            counter++
         }
         effect.repeat(Schedule.delay(25.milliseconds)).run()
         val duration = System.currentTimeMillis() - start
         counter shouldBe 10
         duration.shouldBeBetween(250, 350)
      }

      test("Schedule.delay should run until an error with delay calculated from index function") {
         var counter = 0
         val start = System.currentTimeMillis()
         val effect = IO.effect {
            counter++
            if (counter == 10)
               error("stop")
            counter
         }
         effect.repeat(Schedule.delay { if (it < 3) 100.milliseconds else Duration.ZERO }).run()
         val duration = System.currentTimeMillis() - start
         counter shouldBe 10
         duration.shouldBeBetween(300, 375)
      }
   }
}
