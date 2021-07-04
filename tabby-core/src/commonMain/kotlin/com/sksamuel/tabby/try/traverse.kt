package com.sksamuel.tabby.`try`

/**
 * Given the collection of [Try]s will traverse and return the first error, or if all are
 * successful, will return a list of the successful values.
 */
fun <A> Try.Companion.traverse(vararg ts: Try<A>): Try<List<A>> = traverse(ts.asList())

/**
 * Given the collection of [Try]s will traverse and return the first error, or if all are
 * successful, will return a list of the successful values.
 */
fun <A> Try.Companion.traverse(ts: Collection<Try<A>>): Try<List<A>> {
   return ts.fold(emptyList()) { acc: List<A>, t ->
      when (t) {
         is Try.Failure -> return t
         is Try.Success -> acc + t.value
      }
   }.success()
}

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
