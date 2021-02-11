package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.failure
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

/**
 * Returns an effect that will decorate this effect with a timeout,
 * setting the result to a [TimeoutCancellationException]
 * in the case of a timeout.
 */
fun <A> IO<A>.timeout(duration: Duration): IO<A> = timeout(duration.toLongMilliseconds())

fun <A> IO<A>.timeout(millis: Long): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      return try {
         withTimeout(millis) { this@timeout.apply() }
      } catch (e: TimeoutCancellationException) {
         e.failure()
      }
   }
}


/**
 * Returns an effect that will decorate this effect with a timeout,
 * setting the result to the given error in the case of a timeout.
 */
fun <A> IO<A>.timeout(duration: Duration, f: () -> Throwable): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      return try {
         withTimeout(duration.toLongMilliseconds()) { this@timeout.apply() }
      } catch (e: TimeoutCancellationException) {
         f().failure()
      }
   }
}
