@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.sksamuel.tabby.effects

import com.sksamuel.tabby.effects.Decision.Halt
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.nanoseconds

/**
 * A [Decision] is the outcome of a [Schedule].
 */
sealed interface Decision {

   /**
    * A decision to halt the effect.
    */
   object Halt : Decision

   /**
    * A decision to re-run the effect after the given delay.
    * The delay can be zero.
    *
    * The [next] schedule is provided.
    */
   data class Continue(
      val delay: Duration,
      val next: Schedule
   ) : Decision
}

/**
 * Returns a new [Decision] which is the result of this decision
 * with the given [duration] added.
 */
fun Decision.plusDuration(duration: Duration): Decision {
   return when (this) {
      is Decision.Continue -> Decision.Continue(this.delay + duration, this.next)
      is Halt -> this
   }
}

/**
 * A Schedule returns a [Decision]. A schedule can decide to [Halt] the effect, or
 * [Continue] the effect with a delay. The delay can be zero.
 */
fun interface Schedule {

   fun decide(): Decision

   class Delay(private val duration: Duration) : Schedule {
      override fun decide(): Decision = Decision.Continue(duration, this)
   }

   class DelayF(private val f: (Int) -> Duration, private val iteration: Int) : Schedule {
      override fun decide(): Decision = Decision.Continue(f(iteration), DelayF(f, iteration + 1))
   }

   object Forever : Schedule {
      override fun decide(): Decision = Decision.Continue(Duration.ZERO, this)
   }

   object Never : Schedule {
      override fun decide(): Decision = Halt
   }

   object Once : Schedule {
      override fun decide(): Decision = Decision.Continue(Duration.ZERO, Never)
   }

   class Loop(private val m: Int, private val n: Int) : Schedule {
      override fun decide(): Decision {
         return if (m < n) Decision.Continue(Duration.ZERO, Loop(m + 1, n)) else Halt
      }
   }

   class WhileTrue(private val predicate: () -> Boolean) : Schedule {
      override fun decide(): Decision {
         return if (predicate()) Decision.Continue(Duration.ZERO, this) else Halt
      }
   }

   companion object {

      /**
       * Returns a [Schedule] that never replays an effect.
       */
      val never: Schedule = Never

      /**
       * Returns a [Schedule] that will decide to continue once, and then halt.
       */
      val once: Schedule = Once

      /**
       * Returns a [Schedule] that will decide to coninue forever, with no delay.
       */
      val forever: Schedule = Forever

      /**
       * Returns a new [Schedule] that continues forever and delays for the given duration
       * between effects.
       */
      fun delay(duration: Duration): Schedule = Delay(duration)

      fun exponential(duration: Duration, factor: Double): Schedule =
         delay { (duration.inMilliseconds * (factor * it + 1)).toLong().milliseconds }

      /**
       * Returns a new [Schedule] that continues forever and delays a duration calculated
       * by the given function [f], which is passed the iteration count.
       *
       * The first iteration count will be zero.
       */
      fun delay(f: (Int) -> Duration): Schedule = DelayF(f, 0)

      /**
       * Returns a [Schedule] that repeats k times.
       */
      fun iterations(k: Int): Schedule = Loop(0, k)

      /**
       * Returns a [Schedule] that evals to [Decision.Continue] while the given [predicate]
       * returns true.
       */
      fun whileTrue(predicate: () -> Boolean): Schedule = WhileTrue(predicate)
   }
}

/**
 * Returns a new [Schedule] that randomly modifies the size of the delays of this schedule.
 */
fun Schedule.jittered(): Schedule = jittered(0.0, 2.0)

/**
 * Returns a new [Schedule] that randomly modifies the size of the delays of this schedule.
 */
fun Schedule.jittered(min: Double, max: Double): Schedule = delayM {
   (it.inNanoseconds * Random.nextDouble(min, max)).nanoseconds
}

/**
 * Returns a new [Schedule] that decorates this schedule by adding
 * the given [duration] to each decision.
 */
fun Schedule.delay(duration: Duration): Schedule = Schedule {
   when (val decision = this@delay.decide()) {
      is Halt -> decision
      is Decision.Continue -> {
         Decision.Continue(decision.delay + duration, decision.next.delay(duration))
      }
   }
}

/**
 * Returns a new [Schedule] that decorates this schedule by modifiying the
 * decision using the given function [f].
 */
fun Schedule.delayM(f: (Duration) -> Duration): Schedule = Schedule {
   when (val decision = this@delayM.decide()) {
      is Halt -> decision
      is Decision.Continue -> Decision.Continue(f(decision.delay), decision.next.delayM(f))
   }
}
