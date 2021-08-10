package com.sksamuel.tabby.results

/**
 * Traverse a collection of [Result]s, discarding failures and collecting successes,
 * return those successes as a List.
 */
fun <A> Result.Companion.collect(results: Collection<Result<A>>): List<A> {
   return results.fold(emptyList()) { acc, op ->
      op.fold({ acc + it }, { acc })
   }
}
