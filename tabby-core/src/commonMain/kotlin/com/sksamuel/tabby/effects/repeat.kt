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

fun <A> IO<A>.repeatWhile(schedule: Schedule, predicate: (Try<A>) -> Boolean): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      var result: Try<A> = this@repeatWhile.apply()
      var next = schedule
      while (predicate(result)) {
         when (val decision = next.decide()) {
            is Decision.Continue -> {
               decision.duration.forEach { delay(it) }
               result = this@repeatWhile.apply()
               next = decision.next
            }
            is Decision.Halt -> return result
         }
      }
      return result
   }
}
