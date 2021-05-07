package com.sksamuel.tabby.`try`

import com.sksamuel.tabby.Tuple13

fun <A, B, C> Try<A>.mapN(other: Try<B>, f: (A, B) -> C): Try<C> {
   return this.flatMap { b ->
      other.map { c -> f(b, c) }
   }
}

fun <A, B, C> Try<A>.mapNWith(other: Try<B>, f: (A, B) -> Try<C>): Try<C> {
   return this.flatMap { a ->
      other.flatMap { b -> f(a, b) }
   }
}


inline fun <A, B, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   fn: (A, B) -> R,
): Try<R> {
   return tuple(a, b, unit, unit, unit, unit, unit, unit, unit, unit, unit, unit, unit).map { fn(it.a, it.b) }
}

inline fun <A, B, C, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   fn: (A, B, C) -> R,
): Try<R> {
   return tuple(a, b, c, unit, unit, unit, unit, unit, unit, unit, unit, unit, unit).map { fn(it.a, it.b, it.c) }
}

inline fun <A, B, C, D, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   fn: (A, B, C, D) -> R,
): Try<R> {
   return tuple(a, b, c, d, unit, unit, unit, unit, unit, unit, unit, unit, unit).map { fn(it.a, it.b, it.c, it.d) }
}

inline fun <A, B, C, D, E, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   fn: (A, B, C, D, E) -> R,
): Try<R> {
   return tuple(a, b, c, d, e, unit, unit, unit, unit, unit, unit, unit, unit).map { fn(it.a, it.b, it.c, it.d, it.e) }
}

inline fun <A, B, C, D, E, F, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   f: Try<F>,
   fn: (A, B, C, D, E, F) -> R,
): Try<R> {
   return tuple(a, b, c, d, e, f, unit, unit, unit, unit, unit, unit, unit).map {
      fn(
         it.a,
         it.b,
         it.c,
         it.d,
         it.e,
         it.f,
      )
   }
}

inline fun <A, B, C, D, E, F, G, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   f: Try<F>,
   g: Try<G>,
   fn: (A, B, C, D, E, F, G) -> R,
): Try<R> {
   return tuple(a, b, c, d, e, f, g, unit, unit, unit, unit, unit, unit).map {
      fn(
         it.a,
         it.b,
         it.c,
         it.d,
         it.e,
         it.f,
         it.g,
      )
   }
}

inline fun <A, B, C, D, E, F, G, H, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   f: Try<F>,
   g: Try<G>,
   h: Try<H>,
   fn: (A, B, C, D, E, F, G, H) -> R,
): Try<R> {
   return tuple(a, b, c, d, e, f, g, h, unit, unit, unit, unit, unit).map {
      fn(
         it.a,
         it.b,
         it.c,
         it.d,
         it.e,
         it.f,
         it.g,
         it.h,
      )
   }
}


inline fun <A, B, C, D, E, F, G, H, I, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   f: Try<F>,
   g: Try<G>,
   h: Try<H>,
   i: Try<I>,
   fn: (A, B, C, D, E, F, G, H, I) -> R,
): Try<R> {
   return tuple(a, b, c, d, e, f, g, h, i, unit, unit, unit, unit).map {
      fn(
         it.a,
         it.b,
         it.c,
         it.d,
         it.e,
         it.f,
         it.g,
         it.h,
         it.i,
      )
   }
}


inline fun <A, B, C, D, E, F, G, H, I, J, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   f: Try<F>,
   g: Try<G>,
   h: Try<H>,
   i: Try<I>,
   j: Try<J>,
   fn: (A, B, C, D, E, F, G, H, I, J) -> R,
): Try<R> {
   return tuple(a, b, c, d, e, f, g, h, i, j, unit, unit, unit).map {
      fn(
         it.a,
         it.b,
         it.c,
         it.d,
         it.e,
         it.f,
         it.g,
         it.h,
         it.i,
         it.j,
      )
   }
}


inline fun <A, B, C, D, E, F, G, H, I, J, K, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   f: Try<F>,
   g: Try<G>,
   h: Try<H>,
   i: Try<I>,
   j: Try<J>,
   k: Try<K>,
   fn: (A, B, C, D, E, F, G, H, I, J, K) -> R,
): Try<R> {
   return tuple(a, b, c, d, e, f, g, h, i, j, k, unit, unit).map {
      fn(
         it.a,
         it.b,
         it.c,
         it.d,
         it.e,
         it.f,
         it.g,
         it.h,
         it.i,
         it.j,
         it.k,
      )
   }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   f: Try<F>,
   g: Try<G>,
   h: Try<H>,
   i: Try<I>,
   j: Try<J>,
   k: Try<K>,
   l: Try<L>,
   fn: (A, B, C, D, E, F, G, H, I, J, K, L) -> R,
): Try<R> {
   return tuple(a, b, c, d, e, f, g, h, i, j, k, l, unit).map {
      fn(
         it.a,
         it.b,
         it.c,
         it.d,
         it.e,
         it.f,
         it.g,
         it.h,
         it.i,
         it.j,
         it.k,
         it.l,
      )
   }
}


inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   f: Try<F>,
   g: Try<G>,
   h: Try<H>,
   i: Try<I>,
   j: Try<J>,
   k: Try<K>,
   l: Try<L>,
   m: Try<M>,
   fn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> R,
): Try<R> {
   return tuple(a, b, c, d, e, f, g, h, i, j, k, l, m).map {
      fn(
         it.a,
         it.b,
         it.c,
         it.d,
         it.e,
         it.f,
         it.g,
         it.h,
         it.i,
         it.j,
         it.k,
         it.l,
         it.m,
      )
   }
}


fun <A, B, C, D, E, F, G, H, I, J, K, L, M> tuple(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   f: Try<F>,
   g: Try<G>,
   h: Try<H>,
   i: Try<I>,
   j: Try<J>,
   k: Try<K>,
   l: Try<L>,
   m: Try<M>,
): Try<Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M>> {
   if (a.isFailure) return a as Try.Failure
   if (b.isFailure) return b as Try.Failure
   if (c.isFailure) return c as Try.Failure
   if (d.isFailure) return d as Try.Failure
   if (e.isFailure) return e as Try.Failure
   if (f.isFailure) return f as Try.Failure
   if (g.isFailure) return g as Try.Failure
   if (h.isFailure) return h as Try.Failure
   if (i.isFailure) return i as Try.Failure
   if (j.isFailure) return j as Try.Failure
   if (k.isFailure) return k as Try.Failure
   if (l.isFailure) return l as Try.Failure
   if (m.isFailure) return m as Try.Failure
   return Tuple13(
      a.getValueUnsafe(),
      b.getValueUnsafe(),
      c.getValueUnsafe(),
      d.getValueUnsafe(),
      e.getValueUnsafe(),
      f.getValueUnsafe(),
      g.getValueUnsafe(),
      h.getValueUnsafe(),
      i.getValueUnsafe(),
      j.getValueUnsafe(),
      k.getValueUnsafe(),
      l.getValueUnsafe(),
      m.getValueUnsafe(),
   ).success()
}
