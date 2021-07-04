package com.sksamuel.tabby.`try`

/**
 * Traverse the collection of [Try]s, discarding failures and returning successes
 * as a list.
 */
fun <A> Try.Companion.collect(vararg ts: Try<A>): List<A> = collect(ts.asList())

fun <A> Try.Companion.collect(ts: Collection<Try<A>>): List<A> {
   return ts.fold(emptyList()) { acc, t ->
      t.fold({ acc }, { acc + it })
   }
}
