package com.sksamuel.tabby.eq

/**
 * Typeclass for equality.
 */
fun interface Eq<A> {
   fun eq(a1: A, a2: A): Boolean
   fun neq(a1: A, a2: A): Boolean = !eq(a1, a2)
}

fun <A> List<A>.any(a: A, eq: Eq<A>): Boolean = this.any { eq.eq(it, a) }
fun <A> List<A>.none(a: A, eq: Eq<A>): Boolean = this.none { eq.eq(it, a) }
