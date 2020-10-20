package com.sksamuel.tabby.io

import com.sksamuel.tabby.Either
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime

/**
 * Returns a new effect that repeats this effect according to the specified
 * schedule or until the first failure. Scheduled recurrences are in addition
 * to the first execution, so that `io.repeat(Schedule.once)` yields an
 * effect that executes `io`, and then if that succeeds, executes `io` an
 * additional time.
 */
@OptIn(ExperimentalTime::class)
fun <E, T> IO<E, T>.repeat(schedule: Schedule<T>): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> {
      var result: Either<E, T> = this@repeat.apply()
      var next = schedule
      while (result.isRight) {
         when (val decision = next.invoke(result.getRightUnsafe())) {
            is Decision.Continue -> {
               decision.duration.forEach { delay(it) }
               result = this@repeat.apply()
               next = decision.next
            }
            is Decision.Halt -> return result
         }
      }
      return result
   }
}
