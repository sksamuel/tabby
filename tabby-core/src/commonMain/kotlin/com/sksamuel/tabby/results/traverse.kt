package com.sksamuel.tabby.results

/**
 * Given a collection of [Result]s will traverse and return the first error, or if all are
 * successful, will return a list of the successful values.
 */
fun <A> Result.Companion.traverse(ts: Collection<Result<A>>): Result<List<A>> {
   return ts.map {
      if (it.isFailure) return it.exceptionOrNull()!!.failure()
      else it.getOrThrow()
   }.success()
}
