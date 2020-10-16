package com.sksamuel.tabby.io

import com.sksamuel.tabby.Either
import com.sksamuel.tabby.Option
import com.sksamuel.tabby.left
import com.sksamuel.tabby.right
import com.sksamuel.tabby.some
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

/**
 * Introduce a delay to this scheduler, adding [interval] between executions.
 */
@OptIn(ExperimentalTime::class)
fun <T> Schedule<T>.delay(interval: Duration): Schedule<T> = delay { interval }

/**
 * Introduce a delay to this scheduler, invoking the given function to calculate the delay
 */
@OptIn(ExperimentalTime::class)
fun <T> Schedule<T>.delay(interval: (T) -> Duration): Schedule<T> = object : Schedule<T> {
   override suspend fun schedule(): (T) -> Option<Duration> {
      val underlying = this@delay.schedule()
      return { t -> underlying(t).map { it + interval(t) } }
   }
}

@OptIn(ExperimentalTime::class)
fun <T> Schedule<T>.delayIf(interval: Duration, predicate: (T) -> Boolean) = object : Schedule<T> {
   override suspend fun schedule(): (T) -> Option<Duration> = {
      if (predicate(it)) interval.some() else 0.milliseconds.some()
   }
}

fun <E, T> IO<E, T>.retryN(attempts: Int, interval: Long): IO<E, T> {
   require(attempts > 0)
   return object : IO<E, T>() {
      override suspend fun apply(): Either<E, T> = attempt(attempts - 1)
      private suspend fun attempt(remaining: Int): Either<E, T> {
         return this@retryN.apply().fold(
            {
               if (remaining > 0) {
                  delay(interval)
                  attempt(remaining - 1)
               } else {
                  it.left()
               }
            },
            { it.right() }
         )
      }
   }
}
