package com.sksamuel.tabby.effects

import com.sksamuel.tabby.results.failure
import com.sksamuel.tabby.results.success
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeBetween
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ScheduleForeverTest : FunSpec() {
   init {

      test("Schedule.delay should run until an error with added delay") {
         var counter = 0
         val start = System.currentTimeMillis()
         repeat(Schedule.delay(100.milliseconds)) {
            counter++
            if (counter == 10)
               "stop".failure()
            else
               "go".success()
         }
         val duration = System.currentTimeMillis() - start
         counter shouldBe 10
         duration.shouldBeBetween(750, 1250)
      }

      test("Schedule.delay should run until an error with added delay from function") {
         var counter = 0
         val start = System.currentTimeMillis()
         repeat(Schedule.delay(25.milliseconds)) {
            if (counter == 10)
               "stop".failure()
            else {
               counter++
               "go".success()
            }
         }
         val duration = System.currentTimeMillis() - start
         counter shouldBe 10
         duration.shouldBeBetween(250, 350)
      }

      test("Schedule.delay should run until an error with delay calculated from index function") {
         var counter = 0
         val start = System.currentTimeMillis()
         repeat(Schedule.delay { if (it < 3) 100.milliseconds else Duration.ZERO }) {
            counter++
            if (counter == 10)
               "stop".failure()
            else
               "go".success()
         }
         val duration = System.currentTimeMillis() - start
         counter shouldBe 10
         duration.shouldBeBetween(300, 375)
      }
   }
}
