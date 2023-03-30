package com.sksamuel.tabby.results

/**
 * Maps this [Result] if it is a sucess that does not contain null. If the Result is a failure,
 * or contains null, returns as is.
 */
inline fun <A, B> Result<A?>.mapIfNotNull(fn: (A) -> B): Result<B?> =
   map { if (it == null) null else fn(it) }

inline fun <A> Result<A?>.mapIfNull(fn: () -> A): Result<A> =
   map { it ?: fn() }

@Deprecated("use mapFailure", ReplaceWith("mapFailure(f)"))
inline fun <A> Result<A>.mapError(f: (Throwable) -> Throwable): Result<A> = mapFailure(f)

inline fun <A> Result<A>.mapFailure(f: (Throwable) -> Throwable): Result<A> =
   fold({ it.success() }, { f(it).failure() })

inline fun <A, B> Result<A>.mapIf(p: (A) -> Boolean, f: (A) -> A): Result<A> =
   map { if (p(it)) f(it) else it }

inline fun <A, B> Result<A>.mapIf(p: (A) -> Boolean, f: (A) -> B, g: (A) -> B): Result<B> =
   map { if (p(it)) f(it) else g(it) }

