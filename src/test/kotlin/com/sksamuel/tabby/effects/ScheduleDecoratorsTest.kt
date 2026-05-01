package com.sksamuel.tabby.effects

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ScheduleDecoratorsTest : FunSpec() {
   init {

      test("Schedule.delay(duration) extension adds a constant delay to each Continue") {
         val base = Schedule.iterations(3)
         val delays = collectDelays(base.delay(50.milliseconds), 5)
         delays shouldBe List(3) { 50.milliseconds }
      }

      test("Schedule.delay(duration) extension preserves Halt") {
         val base = Schedule.never
         val delays = collectDelays(base.delay(50.milliseconds), 3)
         delays shouldBe emptyList()
      }

      test("Schedule.delay(duration) extension stacks additively") {
         val base = Schedule.iterations(2)
         val delays = collectDelays(base.delay(50.milliseconds).delay(25.milliseconds), 5)
         delays shouldBe List(2) { 75.milliseconds }
      }

      test("Schedule.delayM applies the modifier to every iteration's delay") {
         val base = Schedule.delay(100.milliseconds)
         val doubled = base.delayM { it * 2 }
         val delays = collectDelays(doubled, 4)
         delays shouldBe List(4) { 200.milliseconds }
      }

      test("Schedule.delayM applies a per-call modifier") {
         val base = Schedule.delay { i -> ((i + 1) * 100).milliseconds }
         val plus10 = base.delayM { it + 10.milliseconds }
         val delays = collectDelays(plus10, 4)
         delays shouldBe listOf(
            110.milliseconds,
            210.milliseconds,
            310.milliseconds,
            410.milliseconds,
         )
      }

      test("Schedule.iterations(0) halts immediately") {
         val delays = collectDelays(Schedule.iterations(0), 5)
         delays shouldBe emptyList()
      }

      test("Schedule.iterations(k) emits exactly k Continues") {
         val delays = collectDelays(Schedule.iterations(4), 10)
         delays.size shouldBe 4
      }

      test("Schedule.never always halts") {
         val delays = collectDelays(Schedule.never, 5)
         delays shouldBe emptyList()
      }

      test("Schedule.once emits one Continue then halts") {
         val delays = collectDelays(Schedule.once, 5)
         delays shouldBe listOf(Duration.ZERO)
      }

      test("Schedule.forever emits unbounded Continues with zero delay") {
         val delays = collectDelays(Schedule.forever, 5)
         delays shouldBe List(5) { Duration.ZERO }
      }

      test("Schedule.whileTrue continues while predicate, then halts") {
         var count = 0
         val schedule = Schedule.whileTrue { count++ < 3 }
         val delays = collectDelays(schedule, 10)
         delays.size shouldBe 3
      }

      test("Decision.plusDuration adds to a Continue's delay") {
         val cont = Decision.Continue(50.milliseconds, Schedule.never)
         val plused = cont.plusDuration(25.milliseconds)
         (plused as Decision.Continue).delay shouldBe 75.milliseconds
      }

      test("Decision.plusDuration is a no-op on Halt") {
         Decision.Halt.plusDuration(25.milliseconds) shouldBe Decision.Halt
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
