package com.sksamuel.tabby.results

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
