package com.sksamuel.tabby.`try`

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
   if (a.isFailure) return a as Try<R>
   if (b.isFailure) return b as Try<R>
   return fn(a.getValueUnsafe(), b.getValueUnsafe()).value()
}

inline fun <A, B, C, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   fn: (A, B, C) -> R,
): Try<R> {
   if (a.isFailure) return a as Try<R>
   if (b.isFailure) return b as Try<R>
   if (c.isFailure) return c as Try<R>
   return fn(a.getValueUnsafe(), b.getValueUnsafe(), c.getValueUnsafe()).value()
}

inline fun <A, B, C, D, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   fn: (A, B, C, D) -> R,
): Try<R> {
   if (a.isFailure) return a as Try<R>
   if (b.isFailure) return b as Try<R>
   if (c.isFailure) return c as Try<R>
   if (d.isFailure) return d as Try<R>
   return fn(a.getValueUnsafe(), b.getValueUnsafe(), c.getValueUnsafe(), d.getValueUnsafe()).value()
}

inline fun <A, B, C, D, E, R> Try.Companion.mapN(
   a: Try<A>,
   b: Try<B>,
   c: Try<C>,
   d: Try<D>,
   e: Try<E>,
   fn: (A, B, C, D, E) -> R,
): Try<R> {
   if (a.isFailure) return a as Try<R>
   if (b.isFailure) return b as Try<R>
   if (c.isFailure) return c as Try<R>
   if (d.isFailure) return d as Try<R>
   if (e.isFailure) return e as Try<R>
   return fn(a.getValueUnsafe(), b.getValueUnsafe(), c.getValueUnsafe(), d.getValueUnsafe(), e.getValueUnsafe()).value()
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
   if (a.isFailure) return a as Try<R>
   if (b.isFailure) return b as Try<R>
   if (c.isFailure) return c as Try<R>
   if (d.isFailure) return d as Try<R>
   if (e.isFailure) return e as Try<R>
   if (f.isFailure) return f as Try<R>
   return fn(
      a.getValueUnsafe(),
      b.getValueUnsafe(),
      c.getValueUnsafe(),
      d.getValueUnsafe(),
      e.getValueUnsafe(),
      f.getValueUnsafe(),
   ).value()
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
   if (a.isFailure) return a as Try<R>
   if (b.isFailure) return b as Try<R>
   if (c.isFailure) return c as Try<R>
   if (d.isFailure) return d as Try<R>
   if (e.isFailure) return e as Try<R>
   if (f.isFailure) return f as Try<R>
   if (g.isFailure) return g as Try<R>
   return fn(
      a.getValueUnsafe(),
      b.getValueUnsafe(),
      c.getValueUnsafe(),
      d.getValueUnsafe(),
      e.getValueUnsafe(),
      f.getValueUnsafe(),
      g.getValueUnsafe(),
   ).value()
}
