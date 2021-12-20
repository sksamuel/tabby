package com.sksamuel.tabby.results

/**
 * Wraps this value in a failed [Result] if the value is null,
 * otherwise a successful Result.
 */
fun <A> A?.failureIfNull(): Result<A> =
   if (this == null) Result.failure(NoSuchElementException()) else Result.success(this)

/**
 * Wraps this value in a failed [Result] using the given error [message] if the value is null,
 * otherwise a successful Result.
 */
fun <A> A?.failureIfNull(message: String): Result<A> =
   if (this == null) Result.failure(RuntimeException(message)) else Result.success(this)

fun <A> A?.failureIfNull(f: () -> Exception): Result<A> =
   if (this == null) Result.failure(f()) else Result.success(this)

/**
 * If this Result is a success that contains null, then returns a failure containing
 * the given message. Otherwise, returns this.
 */
fun <A> Result<A?>.absolve(message: String): Result<A> = fold(
   { if (it == null) Result.failure(Exception(message)) else Result.success(it) },
   { Result.failure(it) }
)

/**
 * If this Result is a success that contains null, then returns a failure containing
 * the given message. Otherwise, returns this.
 */
fun <A> Result<A?>.absolve(f: () -> Exception): Result<A> = fold(
   { if (it == null) Result.failure(f()) else Result.success(it) },
   { Result.failure(it) }
)

