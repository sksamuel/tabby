package com.sksamuel.tabby.io

import com.sksamuel.tabby.Either
import com.sksamuel.tabby.left
import com.sksamuel.tabby.right
import kotlinx.coroutines.delay

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
