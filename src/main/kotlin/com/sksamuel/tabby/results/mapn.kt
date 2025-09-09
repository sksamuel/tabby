package com.sksamuel.tabby.results

/**
 * If all inputs are successes, then executes the function [fn], otherwise returns
 * the first failed result.
 */
inline fun <A, B, R> Result.Companion.mapN(
   a: Result<A>,
   b: Result<B>,
   fn: (A, B) -> R,
): Result<R> {
   if (a.isFailure) return a as Result<R>
   if (b.isFailure) return b as Result<R>
   return fn(a.getOrThrow(), b.getOrThrow()).success()
}

/**
 * If all inputs are successes, then executes the function [fn], otherwise returns
 * the first failed result.
 */
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

/**
 * If all inputs are successes, then executes the function [fn], otherwise returns
 * the first failed result.
 */
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

/**
 * If all inputs are successes, then executes the function [fn], otherwise returns
 * the first failed result.
 */
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

/**
 * If all inputs are successes, then executes the function [fn], otherwise returns
 * the first failed result.
 */
inline fun <A, B, C, D, E, F, R> Result.Companion.mapN(
   a: Result<A>,
   b: Result<B>,
   c: Result<C>,
   d: Result<D>,
   e: Result<E>,
   f: Result<F>,
   fn: (A, B, C, D, E, F) -> R,
): Result<R> {
   if (a.isFailure) return a as Result<R>
   if (b.isFailure) return b as Result<R>
   if (c.isFailure) return c as Result<R>
   if (d.isFailure) return c as Result<R>
   if (e.isFailure) return c as Result<R>
   if (f.isFailure) return c as Result<R>
   return fn(a.getOrThrow(), b.getOrThrow(), c.getOrThrow(), d.getOrThrow(), e.getOrThrow(), f.getOrThrow()).success()
}
