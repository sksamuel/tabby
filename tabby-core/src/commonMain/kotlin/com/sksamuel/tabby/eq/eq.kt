package com.sksamuel.tabby.eq

/**
 * Typeclass for equality.
 */
fun interface Eq<A> {
   fun eq(a1: A, a2: A): Boolean
   fun neq(a1: A, a2: A): Boolean = !eq(a1, a2)
}
