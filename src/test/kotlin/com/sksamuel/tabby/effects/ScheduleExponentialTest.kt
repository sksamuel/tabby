package com.sksamuel.tabby.effects

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ScheduleExponentialTest : FunSpec() {
   init {

      test("Schedule.exponential should produce delays that grow by the factor each iteration") {
         val schedule = Schedule.exponential(100.milliseconds, 2.0)
         val delays = collectDelays(schedule, 5)
         delays shouldBe listOf(
            100.milliseconds,
            200.milliseconds,
            400.milliseconds,
            800.milliseconds,
            1600.milliseconds,
         )
      }

      test("Schedule.exponential with factor 3.0 should triple the delay each iteration") {
         val schedule = Schedule.exponential(50.milliseconds, 3.0)
         val delays = collectDelays(schedule, 4)
         delays shouldBe listOf(
            50.milliseconds,
            150.milliseconds,
            450.milliseconds,
            1350.milliseconds,
         )
      }

      test("Schedule.exponential with factor 1.0 should keep the delay constant") {
         val schedule = Schedule.exponential(75.milliseconds, 1.0)
         val delays = collectDelays(schedule, 4)
         delays shouldBe List(4) { 75.milliseconds }
      }
   }

   private fun collectDelays(schedule: Schedule, n: Int): List<Duration> {
      val out = mutableListOf<Duration>()
      var current = schedule
      repeat(n) {
         when (val decision = current.decide()) {
            is Decision.Continue -> {
               out += decision.delay
               current = decision.next
            }
            Decision.Halt -> return out
         }
      }
      return out
   }
}
