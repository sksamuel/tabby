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
 * A Schedule is invoked with an error and returns a [Decision] on whether to retry or not.
 */
fun interface Schedule {

   fun invoke(result: Throwable): Decision

   companion object {

      val halt: Decision = Decision.Halt

      val never = Schedule { Decision.Halt }

      fun once(): Schedule = Schedule { Decision.Continue(none, never) }

      /**
       * Returns a [Schedule] that will decide to coninue forever, with no delay.
       */
      fun forever(): Schedule {
         fun loop(): Schedule = Schedule { Decision.Continue(none, loop()) }
         return loop()
      }


      /**
       * Returns a new Schedule that continues forever and delays for the given duration
       * between effects.
       */
      fun delay(duration: Duration): Schedule = delay { duration }

      fun delay(f: (Throwable) -> Duration): Schedule = Schedule { Decision.Continue(f(it).some(), delay(f)) }

      /**
       * Returns a new Schedule that continues forever, adding the given delay if the predicate
       * evaluates to true.
       */
      fun delayIf(duration: Duration,
                  test: (Throwable) -> Boolean): Schedule = Schedule {
         val next = delayIf(duration, test)
         if (test(it)) Decision.Continue(duration.some(), next) else Decision.Continue(none, next)
      }

      fun whileTrue(test: (Throwable) -> Boolean): Schedule = Schedule {
         val next = whileTrue(test)
         if (test(it)) Decision.Continue(none, next) else halt
      }

      /**
       * Returns a new schedule that repeats k times.
       */
      fun <T> iterations(k: Int): Schedule {
         fun loop(counter: Int): Schedule = Schedule {
            if (counter < k) Decision.Continue(none, loop(counter + 1)) else halt
         }
         return loop(0)
      }
   }
}

/**
 * Returns a new [Schedule] that randomly modifies the size of the intervals of the given schedule.
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
fun Schedule.delay(duration: Duration): Schedule = delay { duration }

/**
 * Returns a new schedule that decorates this schedule by modifiying the existing delay (if any) using
 * the given function.
 */
fun Schedule.delayM(f: (Option<Duration>) -> Option<Duration>): Schedule = Schedule { result ->
   when (val d = this@delayM.invoke(result)) {
      is Decision.Halt -> d
      is Decision.Continue -> Decision.Continue(f(d.duration), d.next.delayM(f))
   }
}

/**
 * Returns a new Schedule that decorates this schedule, adding the delay returned from the specified
 * function to each decision to continue.
 */
fun Schedule.delay(f: (Throwable) -> Duration): Schedule = Schedule { result ->
   when (val d = this@delay.invoke(result)) {
      is Decision.Halt -> d
      is Decision.Continue -> {
         val duration = d.duration.getOrElse(0.milliseconds) + f(result)
         Decision.Continue(duration.some(), d.next.delay(f))
      }
   }
}

/**
 * Returns a new [Schedule] that decorates this schedule, adding a delay if the predicate
 * evaluates to true, otherwise adding no delay.
 */
fun Schedule.delayIf(duration: Duration,
                     predicate: (Throwable) -> Boolean) = Schedule {
   if (predicate(it)) {
      this@delayIf.invoke(it).plusDuration(duration)
   } else {
      this@delayIf.invoke(it)
   }
}
