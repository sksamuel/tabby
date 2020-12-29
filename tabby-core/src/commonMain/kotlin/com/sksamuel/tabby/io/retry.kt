package com.sksamuel.tabby.io

import com.sksamuel.tabby.either.Either
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime

/**
 * Retries this effect while it is an error, using the given schedule.
 */
@OptIn(ExperimentalTime::class)
fun <E, T> IO<E, T>.retry(schedule: Schedule<E>): IO<E, T> = object : IO<E, T>() {

   override suspend fun apply(): Either<E, T> {
      var result: Either<E, T> = this@retry.apply()
      var next = schedule
      while (result.isLeft) {
         when (val decision = next.invoke(result.getLeftUnsafe())) {
            is Decision.Continue -> {
               decision.duration.forEach { delay(it) }
               result = this@retry.apply()
               next = decision.next
            }
            is Decision.Halt -> return result
         }
      }
      return result
   }
}
