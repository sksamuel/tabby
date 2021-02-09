package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.success

/**
 * For an IO<List<T>>, applies the given function to each element of the list,
 * returning a new IO with the mapped list.
 */
fun <A, B> IO<List<A>>.mapElements(f: (A) -> B): IO<List<B>> {
   return this.map { it.map(f) }
}

/**
 * Returns an effect that will execute the list of IOs in turn, returning the first success.
 *
 * This method short-circuits and each successive IO is not started until the previous one fails,
 * or returns successfully.
 *
 * If all IOs fail, the result of this method is a failed IO, with the last failure.
 */
fun <A> List<IO<A>>.firstOrNone(): IO<A> {
   require(this.isNotEmpty())
   return object : IO<A>() {
      override suspend fun apply(): Try<A> {
         var lastError: Throwable? = null
         this@firstOrNone.forEach {
            when (val result = it.apply()) {
               is Try.Failure -> lastError = result.error
               is Try.Success -> return result.value.success()
            }
         }
         return Try.failure(lastError ?: NoSuchElementException())
      }
   }
}
