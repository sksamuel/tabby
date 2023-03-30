package com.sksamuel.tabby.results

/**
 * Gather's together Result effects.
 *
 * Specifically, returns the first failed Result, or a List of all successful results.
 */
fun <A> Collection<Result<A>>.sequence(): Result<List<A>> {
   return map {
      if (it.isFailure) return it.exceptionOrNull()!!.failure()
      else it.getOrThrow()
   }.success()
}
