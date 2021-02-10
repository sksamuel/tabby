@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.sksamuel.tabby.effects

import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.getOrElse
import com.sksamuel.tabby.option.none
import com.sksamuel.tabby.option.some
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.nanoseconds

sealed class Decision {

   /**
    * A decision to halt the effect.
    */
   object Halt : Decision()

   /**
    * A decision to re-run the effect after the given delay.
    * The delay can be zero.
    *
    * A schedule is provided to evaluate the next loop if the retry also fails.
    */
   data class Continue(val duration: Option<Duration>,
                       val next: Schedule) : Decision()

   fun plusDuration(plus: Duration): Decision {
      return when (this) {
         is Continue -> Continue(this.duration.map { it + plus }, this.next)
         is Halt -> this
      }
   }
}

/**
 * A Schedule is invoked with the  and returns a [Decision] on whether to retry or not.
 */
fun interface Schedule {

   fun decide(): Decision

   class Delay(private val duration: Duration) : Schedule {
      override fun decide(): Decision = Decision.Continue(duration.some(), this)
   }

   object Forever : Schedule {
      override fun decide(): Decision = Decision.Continue(none, this)
   }

   object Never : Schedule {
      override fun decide(): Decision = Decision.Halt
   }

   object Once : Schedule {
      override fun decide(): Decision = Decision.Continue(none, Never)
   }

   class Loop(private val m: Int, private val n: Int) : Schedule {
      override fun decide(): Decision {
         return if (m < n) Decision.Continue(none, Loop(m + 1, n)) else Decision.Halt
      }
   }

   companion object {

      val never: Schedule = Never

      /**
       * Returns a [Schedule] that will decide to continue once, and then halt.
       */
      fun once(): Schedule = Once

      /**
       * Returns a [Schedule] that will decide to coninue forever, with no delay.
       */
      fun forever(): Schedule = Forever

      /**
       * Returns a new Schedule that continues forever and delays for the given duration
       * between effects.
       */
      fun delay(duration: Duration): Schedule = Delay(duration)

      /**
       * Returns a new schedule that repeats k times.
       */
      fun iterations(k: Int): Schedule = Loop(0, k)
   }
}

/**
 * Returns a new [Schedule] that randomly modifies the size of the intervals of this schedule.
 */
fun Schedule.jittered(): Schedule = jittered(0.0, 2.0)

/**
 * Returns a new [Schedule] that randomly modifies the size of the intervals of this schedule.
 */
fun Schedule.jittered(min: Double, max: Double): Schedule = delayM { duration ->
   duration.map { (it.inNanoseconds * Random.nextDouble(min, max)).nanoseconds }
}

/**
 * Returns a new schedule that decorates this schedule, adding the given delay to each decision to continue.
 */
fun Schedule.delay(duration: Duration): Schedule = Schedule {
   when (val decision = this@delay.decide()) {
      is Decision.Halt -> decision
      is Decision.Continue -> {
         val d2 = decision.duration.getOrElse(0.milliseconds) + duration
         Decision.Continue(d2.some(), decision.next.delay(duration))
      }
   }
}

/**
 * Returns a new schedule that decorates this schedule by modifiying the existing delay (if any) using
 * the given function.
 */
fun Schedule.delayM(f: (Option<Duration>) -> Option<Duration>): Schedule = Schedule {
   when (val decision = this@delayM.decide()) {
      is Decision.Halt -> decision
      is Decision.Continue -> Decision.Continue(f(decision.duration), decision.next.delayM(f))
   }
}
