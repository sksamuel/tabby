package com.sksamuel.tabby.results

/**
 * If this [Result] is a failure, returns [other], otherwise returns this.
 */
fun <A> Result<A>.orElse(other: Result<A>): Result<A> = if (this.isFailure) other else this

inline fun <A> Result<A>.orElse(f: () -> Result<A>): Result<A> = if (this.isFailure) f() else this

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
fun <A> Result<A>.failIf(p: (A) -> Boolean) = failIf(p, RuntimeException("failure"))

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
fun <A> Result<A>.failIf(p: (A) -> Boolean, message: String) = failIf(p, RuntimeException(message))

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
fun <A> Result<A>.failIf(p: (A) -> Boolean, exception: Exception) =
   flatMap { if (p(it)) exception.failure() else it.success() }

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
fun <A> Result<A>.failIfNot(p: (A) -> Boolean) = failIfNot(p, "failure")

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
fun <A> Result<A>.failIfNot(p: (A) -> Boolean, message: String) = failIfNot(p, RuntimeException(message))

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
fun <A> Result<A>.failIfNot(p: (A) -> Boolean, exception: Exception) =
   flatMap { if (!p(it)) exception.failure() else it.success() }

/**
 * Returns a successful [Result] which contains Unit.
 */
fun Result.Companion.unit() = Unit.success()

fun <A> Result<Result<A>>.flatten(): Result<A> = this.fold({ it }, { it.failure() })

fun <A> Result<A>.exceptionOrThrow() = this.exceptionOrNull() ?: throw IllegalStateException("Expected exception")

fun <A> Result<A?>.onSuccessIfNotNull(f: (A) -> Unit) = this.onSuccess { if (it != null) f(it) }
