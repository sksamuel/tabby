package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

fun <A> IO<A>.timeout(millis: Long): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      return try {
         withTimeout(millis) { this@timeout.apply() }
      } catch (e: TimeoutCancellationException) {
         Try.failure(e)
      }
   }
}
