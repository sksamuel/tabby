package com.sksamuel.tabby.results

inline fun <A, B, R> Result.Companion.mapN(
   a: Result<A>,
   b: Result<B>,
   fn: (A, B) -> R,
): Result<R> {
   if (a.isFailure) return a as Result<R>
   if (b.isFailure) return b as Result<R>
   return fn(a.getOrThrow(), b.getOrThrow()).success()
}

inline fun <A, B, C, R> Result.Companion.mapN(
   a: Result<A>,
   b: Result<B>,
   c: Result<C>,
   fn: (A, B, C) -> R,
): Result<R> {
   if (a.isFailure) return a as Result<R>
   if (b.isFailure) return b as Result<R>
   if (c.isFailure) return c as Result<R>
   return fn(a.getOrThrow(), b.getOrThrow(), c.getOrThrow()).success()
}

inline fun <A, B, C, D, R> Result.Companion.mapN(
   a: Result<A>,
   b: Result<B>,
   c: Result<C>,
   d: Result<D>,
   fn: (A, B, C, D) -> R,
): Result<R> {
   if (a.isFailure) return a as Result<R>
   if (b.isFailure) return b as Result<R>
   if (c.isFailure) return c as Result<R>
   if (d.isFailure) return c as Result<R>
   return fn(a.getOrThrow(), b.getOrThrow(), c.getOrThrow(), d.getOrThrow()).success()
}

inline fun <A, B, C, D, E, R> Result.Companion.mapN(
   a: Result<A>,
   b: Result<B>,
   c: Result<C>,
   d: Result<D>,
   e: Result<E>,
   fn: (A, B, C, D, E) -> R,
): Result<R> {
   if (a.isFailure) return a as Result<R>
   if (b.isFailure) return b as Result<R>
   if (c.isFailure) return c as Result<R>
   if (d.isFailure) return c as Result<R>
   if (e.isFailure) return c as Result<R>
   return fn(a.getOrThrow(), b.getOrThrow(), c.getOrThrow(), d.getOrThrow(), e.getOrThrow()).success()
}
