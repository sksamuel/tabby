package com.sksamuel.tabby

import kotlinx.coroutines.delay

fun <E, T> IO<E, T>.retry(attempts: Int, interval: Long): IO<E, T> = object : IO<E, T> {

   private suspend fun attempt(remaining: Int): Either<E, T> {
      return this@retry.apply().fold(
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

   override suspend fun apply(): Either<E, T> = attempt(attempts)
}
