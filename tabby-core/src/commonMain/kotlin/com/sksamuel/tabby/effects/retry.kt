package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try

/**
 * Repeats the given effect according to the schedule, or until the effect succeeds.
 */
suspend fun <A> Schedule.retry(f: suspend () -> Try<A>): Try<A> {
   return repeatWhile(this, { f() }) { it.isFailure }
}
