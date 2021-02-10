@file:Suppress("unused")

package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import kotlinx.coroutines.delay

/**
 * Retries this effect while it is an error, using the given schedule.
 */
fun <A> IO<A>.retry(schedule: Schedule): IO<A> = object : IO<A>() {

   override suspend fun apply(): Try<A> {
      var result: Try<A> = this@retry.apply()
      var next = schedule
      while (result.isFailure) {
         when (val decision = next.invoke(result.getErrorUnsafe())) {
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
