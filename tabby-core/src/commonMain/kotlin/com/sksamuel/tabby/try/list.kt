package com.sksamuel.tabby.`try`

fun <A> List<Try<A>>.filterToSuccesses(): List<A> =
   filterIsInstance<Try.Success<A>>().map { it.value }

fun <A> List<Try<A>>.filterToFailures(): List<Throwable> =
   filterIsInstance<Try.Failure>().map { it.error }

/**
 * For a List<Try<A>> will return a Try<List<A>>.
 * If all try's in the list are success then the result is a success of those trys.
 * Otherwise it is a failure of the first error.
 */
fun <A> List<Try<A>>.traverse(): Try<List<A>> {
   val ts = map { it ->
      it.fold(
         { return Try.Failure(it) },
         { it }
      )
   }
   return Try.success(ts)
}
