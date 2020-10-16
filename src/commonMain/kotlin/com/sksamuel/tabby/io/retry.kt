package com.sksamuel.tabby.io

import com.sksamuel.tabby.Either
import com.sksamuel.tabby.Option
import com.sksamuel.tabby.left
import com.sksamuel.tabby.none
import com.sksamuel.tabby.right
import com.sksamuel.tabby.some
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
interface Schedule {

   /**
    * Returns a function, which is used to determine when, or if, to re-run the effect.
    * If none is returned, then the scheduling will conclude.
    * The function will be passed the output of the previous execution.
    */
   suspend fun <T> schedule(): (T) -> Option<Duration>

   /**
    * Execute once after the initial run.
    */
   object Once : Schedule {
      override suspend fun <T> schedule(): (T) -> Option<Duration> {
         var ran = false
         return {
            if (ran) none else {
               ran = true
               0.milliseconds.some()
            }
         }
      }
   }

   /**
    * Executes forever, until the first error.
    */
   object Forever : Schedule {
      override suspend fun <T> schedule(): (T) -> Option<Duration> = { 0.milliseconds.some() }
   }
}

/**
 * Introduce a delay to this scheduler, adding [interval] between executions.
 */
@OptIn(ExperimentalTime::class)
fun Schedule.delay(interval: Duration): Schedule = object : Schedule {
   override suspend fun <T> schedule(): (T) -> Option<Duration> {
      val underlying = this@delay.schedule<T>()
      return { t ->
         delay(interval)
         underlying(t)
      }
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
