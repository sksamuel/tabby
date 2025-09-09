package com.sksamuel.tabby.results

/**
 * Applies a side effecting function [f] to this result, if this result is a success.
 * If [f] fails, then the return of [then] will be the failed value of [f], otherwise,
 * the return of [f] is ignored and the original value is returned.
 */
inline fun <A> Result<A>.then(f: (A) -> Result<*>): Result<A> =
   flatMap { a -> f(a).map { a } }
