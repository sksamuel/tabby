package com.sksamuel.tabby.option

fun <A, B, R> Option.Companion.mapN(
   a: Option<A>,
   b: Option<B>,
   f: (A, B) -> R,
): Option<R> {
   return when {
      a.isEmpty() || b.isEmpty() -> none
      else -> f(a.getUnsafe(), b.getUnsafe()).some()
   }
}

fun <A, B, C, R> Option.Companion.mapN(
   a: Option<A>,
   b: Option<B>,
   c: Option<C>,
   f: (A, B, C) -> R,
): Option<R> {
   return when {
      a.isEmpty() || b.isEmpty() || c.isEmpty() -> none
      else -> f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe()).some()
   }
}

fun <A, B, C, D, R> Option.Companion.mapN(
   a: Option<A>,
   b: Option<B>,
   c: Option<C>,
   d: Option<D>,
   f: (A, B, C, D) -> R,
): Option<R> {
   return when {
      a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() -> none
      else -> f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe()).some()
   }
}

fun <A, B, C, D, E, R> Option.Companion.mapN(
   a: Option<A>,
   b: Option<B>,
   c: Option<C>,
   d: Option<D>,
   e: Option<E>,
   f: (A, B, C, D, E) -> R,
): Option<R> {
   return when {
      a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || e.isEmpty() -> none
      else -> f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe(), e.getUnsafe()).some()
   }
}

fun <A, B, C, D, E, F, R> Option.Companion.mapN(
   a: Option<A>,
   b: Option<B>,
   c: Option<C>,
   d: Option<D>,
   e: Option<E>,
   f: Option<F>,
   fn: (A, B, C, D, E, F) -> R,
): Option<R> {
   return when {
      a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || e.isEmpty() || f.isEmpty() -> none
      else -> fn(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe(), e.getUnsafe(), f.getUnsafe()).some()
   }
}
