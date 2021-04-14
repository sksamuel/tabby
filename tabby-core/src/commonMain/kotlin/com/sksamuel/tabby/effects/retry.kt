package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.catch

/**
 * Repeats the given effect, according to the specified schedule, while the result is a failure.
 */
suspend fun <A> Schedule.retry(f: suspend () -> Try<A>): Try<A> {
   return repeatWhile(this, { f() }) { it.isFailure }
}

/**
 * Repeats the given effect, according to the specified schedule, while it fails to complete normally.
 */
suspend fun <A> Schedule.retrySafe(f: suspend () -> A): Try<A> = retry { catch { f() } }
