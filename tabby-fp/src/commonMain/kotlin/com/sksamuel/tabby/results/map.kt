package com.sksamuel.tabby.results

/**
 * If this [Result] is a success that contains null, will replace the null with the
 * value of the function [f]. Otherwise, returns as is.
 */
@Deprecated("Use replaceNull", ReplaceWith("replaceNull(f)"))
inline fun <A> Result<A?>.withDefault(f: () -> A): Result<A> = replaceNull(f)

/**
 * Maps this [Result] if it is a sucess that does not contain null. If the Result is a failure,
 * or contains null, returns as is.
 */
inline fun <A, B> Result<A?>.mapIfNotNull(fn: (A) -> B): Result<B?> =
   map { if (it == null) null else fn(it) }

inline fun <A> Result<A?>.mapIfNull(fn: () -> A): Result<A> =
   map { it ?: fn() }

inline fun <A, B> Result<A?>.flatMapIfNotNull(fn: (A) -> Result<B>): Result<B?> =
   flatMap { if (it == null) null.success() else fn(it) }

inline fun <A, B> Result<A>.flatMap(fn: (A) -> Result<B>): Result<B> =
   fold({ fn(it) }, { Result.failure(it) })

@Deprecated("use flatMapIfNull", ReplaceWith("flatMapIfNull(fn)"))
inline fun <A> Result<A?>.flatMapNull(fn: () -> Result<A>): Result<A> = flatMapIfNull(fn)

inline fun <A> Result<A?>.flatMapIfNull(fn: () -> Result<A>): Result<A> =
   flatMap { it?.success() ?: fn() }

inline fun <A> Result<A>.mapError(f: (Throwable) -> Throwable): Result<A> =
   fold({ it.success() }, { f(it).failure() })

inline fun <A> Result<A>.flatApply(f: (A) -> Result<*>): Result<A> =
   flatMap { a -> f(a).map { a } }

inline fun <A, B> Result<A>.mapIf(p: (A) -> Boolean, f: (A) -> B, g: (A) -> B): Result<B> =
   map { if (p(it)) f(it) else g(it) }

inline fun <A, B> Result<A>.flatMapIf(p: (A) -> Boolean, f: (A) -> Result<B>, g: (A) -> Result<B>): Result<B> =
   flatMap { if (p(it)) f(it) else g(it) }
