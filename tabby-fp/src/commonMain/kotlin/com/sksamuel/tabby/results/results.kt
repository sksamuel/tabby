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

/**
 * If this [Result] is a failure, returns [other], otherwise returns this.
 */
fun <A> Result<A>.orElse(other: Result<A>): Result<A> = if (this.isFailure) other else this

/**
 * Returns a successful [Result] which contains Unit.
 */
fun Result.Companion.unit() = Unit.success()

fun <A> Result<Result<A>>.flatten(): Result<A> = this.fold({ it }, { it.failure() })

fun <A> Result<A>.exceptionOrThrow() = this.exceptionOrNull() ?: throw IllegalStateException("Expected exception")
