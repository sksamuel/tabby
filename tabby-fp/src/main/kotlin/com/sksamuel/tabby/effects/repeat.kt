package com.sksamuel.tabby.effects

import kotlinx.coroutines.delay

/**
 * Repeats this effect according to the specified schedule or until the first failure.
 *
 * Scheduled recurrences are in addition to the first execution, so for example,
 * `repeat(Schedule.once,f)` executes `f`, and then if that
 * succeeds, executes `f` an additional time.
 */
suspend fun <A> repeat(schedule: Schedule, f: suspend () -> Result<A>): Result<A> =
   repeatWhile(schedule, f) { it.isSuccess }

/**
 * Repeats the given effect according to the schedule, or until the effect succeeds.
 */
suspend fun <A> retry(schedule: Schedule, f: suspend () -> Result<A>): Result<A> =
   repeatWhile(schedule, f) { it.isFailure }


/**
 * Runs the given effect while the predicate is true.
 */
internal suspend fun <A> repeatWhile(
   schedule: Schedule,
   f: suspend () -> Result<A>,
   predicate: (Result<A>) -> Boolean
): Result<A> {
   var result: Result<A> = f()
   var next = schedule
   while (predicate(result)) {
      when (val decision = next.decide()) {
         is Decision.Continue -> {
            delay(decision.delay.inWholeMilliseconds)
            result = f()
            next = decision.next
         }
         is Decision.Halt -> return result
      }
   }
   return result
}
