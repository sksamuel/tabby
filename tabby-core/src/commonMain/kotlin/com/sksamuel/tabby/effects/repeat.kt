package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import kotlinx.coroutines.delay

/**
 * Returns a new effect that repeats this effect according to the specified
 * schedule or until the first failure.
 *
 * Scheduled recurrences are in addition to the first execution, so for example,
 * `io.repeat(Schedule.once)` yields an effect that executes `io`, and then if that
 * succeeds, executes `io` an additional time.
 */
fun <A> IO<A>.repeat(schedule: Schedule): IO<A> = repeatWhile(schedule) { it.isSuccess }

/**
 * Retries this effect while it is an error, using the given schedule.
 */
fun <A> IO<A>.retry(schedule: Schedule): IO<A> = repeatWhile(schedule) { it.isFailure }

/**
 * Returns an effect that will repeat while the predicate is true, using the given schedule.
 */
fun <A> IO<A>.repeatWhile(schedule: Schedule, predicate: (Try<A>) -> Boolean): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      var result: Try<A> = this@repeatWhile.apply()
      var next = schedule
      while (predicate(result)) {
         when (val decision = next.decide()) {
            is Decision.Continue -> {
               decision.duration.forEach { delay(it.toLongMilliseconds()) }
               result = this@repeatWhile.apply()
               next = decision.next
            }
            is Decision.Halt -> return result
         }
      }
      return result
   }
}

/**
 * Returns an effect that will repeat based on the schedule returned by the function.
 * A new schedule is requested on each repeat.
 */
fun <A> IO<A>.repeatWhile(f: (IndexedValue<Try<A>>) -> Schedule): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      var n = 0
      while (true) {
         val result: Try<A> = this@repeatWhile.apply()
         when (val decision = f(IndexedValue(n++, result)).decide()) {
            is Decision.Continue -> decision.duration.forEach { delay(it.toLongMilliseconds()) }
            is Decision.Halt -> return result
         }
      }
   }
}
