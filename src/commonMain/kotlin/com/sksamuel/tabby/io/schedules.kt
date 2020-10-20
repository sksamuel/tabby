package com.sksamuel.tabby.io

import com.sksamuel.tabby.Option
import com.sksamuel.tabby.getOrElse
import com.sksamuel.tabby.none
import com.sksamuel.tabby.some
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.nanoseconds

@OptIn(ExperimentalTime::class)
sealed class Decision<in A> {

   /**
    * A decision to halt the effect.
    */
   object Halt : Decision<Any>()

   /**
    * A decision to re-run the effect after the optional delay.
    * A schedule must be provided to evaluate the next loop.
    */
   data class Continue<in A>(val duration: Option<Duration>,
                             val next: Schedule<A>) : Decision<A>()

   fun plusDuration(plus: Duration): Decision<A> {
      println("Adding duration $plus")
      return when (this) {
         is Continue -> Continue(this.duration.map { it + plus }, this.next)
         is Halt -> this
      }
   }
}

@OptIn(ExperimentalTime::class)
fun interface Schedule<in A> {

   fun invoke(result: A): Decision<A>

   companion object {

      fun <T> halt() = Decision.Halt as Decision<T>

      fun <T> never() = Never as Schedule<T>

      fun <T> once(): Schedule<T> = Schedule { Decision.Continue(none, never()) }

      fun <T> forever(): Schedule<T> {
         fun loop(): Schedule<T> = Schedule { Decision.Continue(none, loop()) }
         return loop()
      }

      object Never : Schedule<Any> {
         override fun invoke(result: Any) = Decision.Halt
      }

      /**
       * Returns a new Schedule that continues forever and delays for the given duration
       * between effects.
       */
      fun <T> delay(duration: Duration): Schedule<T> = delay { duration }

      fun <T> delay(f: (T) -> Duration): Schedule<T> = Schedule { Decision.Continue(f(it).some(), delay(f)) }

      /**
       * Returns a new Schedule that continues forever, adding the given delay if the predicate
       * evaluates to true.
       */
      fun <A> delayIf(duration: Duration,
                      test: (A) -> Boolean): Schedule<A> = Schedule<A> {
         val next = delayIf(duration, test)
         if (test(it)) Decision.Continue(duration.some(), next) else Decision.Continue(none, next)
      }

      fun <A> whileTrue(test: (A) -> Boolean): Schedule<A> = Schedule {
         val next = whileTrue(test)
         if (test(it)) Decision.Continue(none, next) else halt()
      }

      /**
       * Returns a new schedule that repeats k times.
       */
      fun <T> iterations(k: Int): Schedule<T> {
         fun loop(counter: Int): Schedule<T> = Schedule {
            if (counter < k) Decision.Continue(none, loop(counter + 1)) else halt()
         }
         return loop(0)
      }
   }
}

private fun <K> unfold(initial: K, f: (K) -> K): Schedule<K> {
   fun loop(k: K): Schedule<K> = Schedule { Decision.Continue(none, loop(f(k))) }
   return loop(initial)
}


/**
 * Returns a new [Schedule] that randomly modifies the size of the intervals of this schedule.
 */
fun <A> Schedule<A>.jittered(): Schedule<A> = jittered(0.0, 2.0)

/**
 * Returns a new [Schedule] that randomly modifies the size of the intervals of this schedule.
 */
@OptIn(ExperimentalTime::class)
fun <A> Schedule<A>.jittered(min: Double, max: Double): Schedule<A> = delayM { duration ->
   duration.map { (it.inNanoseconds * Random.nextDouble(min, max)).nanoseconds }
}

/**
 * Returns a new schedule that decorates this schedule, adding the given delay to each decision to continue.
 */
@OptIn(ExperimentalTime::class)
fun <A> Schedule<A>.delay(duration: Duration): Schedule<A> = delay { duration }

/**
 * Returns a new schedule that decorates this schedule by modifiying the existing delay (if any) using
 * the given function.
 */
@OptIn(ExperimentalTime::class)
fun <A> Schedule<A>.delayM(f: (Option<Duration>) -> Option<Duration>): Schedule<A> = Schedule<A> { result ->
   when (val d = this@delayM.invoke(result)) {
      is Decision.Halt -> d
      is Decision.Continue -> Decision.Continue(f(d.duration), d.next.delayM(f))
   }
}

/**
 * Returns a new schedule that decorates this schedule, adding the delay returned from the specified
 * function to each decision to continue.
 */
@OptIn(ExperimentalTime::class)
fun <A> Schedule<A>.delay(f: (A) -> Duration): Schedule<A> = Schedule<A> { result ->
   when (val d = this@delay.invoke(result)) {
      is Decision.Halt -> d
      is Decision.Continue -> {
         val duration = d.duration.getOrElse(0.milliseconds) + f(result)
         Decision.Continue(duration.some(), d.next.delay(f))
      }
   }
}

/**
 * Returns a new schedule that decorates this schedule, adding a delay if the test evaluates to true,
 * otherwise adding no delay.
 */
@OptIn(ExperimentalTime::class)
fun <A> Schedule<A>.delayIf(duration: Duration,
                            test: (A) -> Boolean) = Schedule<A> {
   if (test(it)) {
      this@delayIf.invoke(it).plusDuration(duration)
   } else {
      this@delayIf.invoke(it)
   }
}
