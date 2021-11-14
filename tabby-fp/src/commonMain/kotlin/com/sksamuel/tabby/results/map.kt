package com.sksamuel.tabby.results

/**
 * If this [Result] is a success that contains null, will replace the null with the
 * value of the function [f]. Otherwise, returns as is..
 */
inline fun <A> Result<A?>.withDefault(f: () -> A): Result<A> {
   return this.fold({ it?.success() ?: f().success() }, { it.failure() })
}

/**
 * Maps this [Result] if it is a sucess that does not contain null. If the Result is a failure,
 * or contains null, returns as is.
 */
inline fun <A, B> Result<A?>.mapIfNotNull(fn: (A) -> B): Result<B?> =
   map { if (it == null) null else fn(it) }

inline fun <A, B> Result<A?>.flatMapIfNotNull(fn: (A) -> Result<B>): Result<B?> =
   flatMap { if (it == null) null.success() else fn(it) }

inline fun <A, B> Result<A>.flatMap(fn: (A) -> Result<B>): Result<B> =
   fold({ fn(it) }, { Result.failure(it) })

inline fun <A> Result<A?>.flatMapNull(fn: () -> Result<A>): Result<A> =
   flatMap { it?.success() ?: fn() }

