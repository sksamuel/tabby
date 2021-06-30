package com.sksamuel.tabby.eq

/**
 * Typeclass for equality.
 */
fun interface Eq<T> {

   fun eq(t1: T, t2: T): Boolean
   fun neq(t1: T, t2: T): Boolean = !eq(t1, t2)

   /**
    * Returns true if the input [t] matches any of the elements of the given list [ts] when
    * comparing using [eq] of this typeclass.
    *
    * @param t the needle to look for
    * @param ts the haystack for search in
    *
    * @return true if ts contains t using eq of this typeclass.
    *
    */
   fun any(t: T, ts: List<T>) = ts.any { eq(it, t) }

   fun none(t: T, ts: List<T>) = ts.none { eq(it, t) }
}

fun <A> List<A>.any(a: A, eq: Eq<A>): Boolean = this.any { eq.eq(it, a) }
fun <A> List<A>.none(a: A, eq: Eq<A>): Boolean = this.none { eq.eq(it, a) }
