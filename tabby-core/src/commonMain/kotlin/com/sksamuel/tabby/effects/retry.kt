package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.catch

/**
 * Repeats the given effect, according to the specified schedule, until it completes normally.
 */
suspend fun Schedule.retryUnit(f: suspend () -> Unit) {
   repeatWhile(this, { catch { f() } }) { it.isFailure }
}

/**
 * Repeats the given effect, according to the specified schedule, while the result is a failure.
 */
suspend fun <A> Schedule.retry(f: suspend () -> Try<A>): Try<A> {
   return repeatWhile(this, { f() }) { it.isFailure }
}
