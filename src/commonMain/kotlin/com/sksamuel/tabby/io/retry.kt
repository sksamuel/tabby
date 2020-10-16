package com.sksamuel.tabby.io

import com.sksamuel.tabby.Either
import com.sksamuel.tabby.left
import com.sksamuel.tabby.recoverWith
import com.sksamuel.tabby.right
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
sealed class Schedule {

   /**
    * Execute once and do not retry.
    */
   object Once : Schedule() {
      override suspend fun <E, T> schedule(effect: IO<E, T>): Either<E, T> = effect.apply()
   }

   /**
    * Repeat once, after the given delay has elapsed.
    */
   class Delayed(private val duration: Duration) : Schedule() {
      override suspend fun <E, T> schedule(effect: IO<E, T>): Either<E, T> {
         return effect.apply().recoverWith {
            delay(duration)
            effect.apply()
         }
      }
   }

   abstract suspend fun <E, T> schedule(effect: IO<E, T>): Either<E, T>
}

fun <E, T> IO<E, T>.retry(policy: Schedule) = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> = policy.schedule(this)
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
