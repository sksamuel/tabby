package com.sksamuel.tabby.results

/**
 * Discards any failed Result's in the collection,
 * returning only the successful values.
 */
@Deprecated("Use collectSuccess", ReplaceWith("collectSuccess()"))
fun <A> Collection<Result<A>>.successes(): List<A> = collectSuccess()

/**
 * Discards any failed Result's in the collection,
 * returning only the successful values.
 */
fun <A> Collection<Result<A>>.collectSuccess(): List<A> {
   return fold(emptyList()) { acc, op ->
      op.fold({ acc + it }, { acc })
   }
}

/**
 * Discards any successful Result's in the collection,
 * returning only the errors.
 */
@Deprecated("Use collectFailure", ReplaceWith("collectFailure()"))
fun <A> Collection<Result<A>>.failures(): List<Throwable> = collectFailure()

/**
 * Discards any successful Result's in the collection, returning only the errors.
 */
fun <A> Collection<Result<A>>.collectFailure(): List<Throwable> {
   return fold(emptyList()) { acc, op ->
      op.fold({ acc }, { acc + it })
   }
}

/**
 * Returns a pair of two lists - the first containing all errors, and the second
 * containing all successes.
 */
fun <A> Collection<Result<A>>.collect(): Pair<List<Throwable>, List<A>> {
   val (failures, successes) = this.partition { it.isFailure }
   val throwables = failures.mapNotNull { it.exceptionOrNull() }
   val values = successes.mapNotNull { it.getOrNull() }
   return Pair(throwables, values)
}

/**
 * Applies [f] to each element, then returns a pair of two lists - the first
 * containing all errors produced by [f], and the second containing all
 * successful values produced by [f].
 */
fun <A, B> Collection<A>.collectBy(f: (A) -> Result<B>): Pair<List<Throwable>, List<B>> =
   map(f).collect()
