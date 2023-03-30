package com.sksamuel.tabby.results

/**
 * If this [Result] is a failure, returns [other], otherwise returns this.
 */
fun <A> Result<A>.orElse(other: Result<A>): Result<A> =
   if (this.isFailure) other else this

inline fun <A> Result<A>.orElse(f: () -> Result<A>): Result<A> =
   if (this.isFailure) f() else this

/**
 * Returns a successful [Result] which contains Unit.
 */
fun Result.Companion.unit() = Unit.success()

/**
 * If this [Result] is a success, returns a new result containing [Unit],
 * otherwise returns the receiver.
 */
fun <A> Result<A>.omit(): Result<Unit> = this.flatMap { Unit.success() }

fun <A> Result<Result<A>>.flatten(): Result<A> = this.fold({ it }, { it.failure() })

/**
 * If this [Result] is a failure, returns the failure exception, otherwise throws an
 * [IllegalStateException].
 */
fun <A> Result<A>.exceptionOrThrow(): Throwable =
   this.exceptionOrNull() ?: throw IllegalStateException("Expected exception")

/**
 * Executes this side effecting function and returns the receiver
 */
inline fun <A> Result<A>.onEach(f: () -> Unit) = this.onSuccess { f() }.onFailure { f() }
