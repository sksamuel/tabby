package com.sksamuel.tabby.results

/**
 * If this [Result] is a success containing null, returns a failure with the given message.
 * Otherwise, returns the receiver.
 */
fun <A> Result<A?>.failIfNull(message: String): Result<A> = failIfNull { Exception(message) }

/**
 * If this [Result] is a success containing null, returns a failure with the given exception.
 * Otherwise, returns the receiver.
 */
inline fun <A> Result<A?>.failIfNull(fn: () -> Exception): Result<A> =
   flatMap { it?.success() ?: Result.failure(fn()) }

/**
 * If this [Result] is a success containing null, returns a failure with a [NoSuchElementException].
 * Otherwise, returns the receiver.
 */
fun <A> Result<A?>.failIfNull() = failIfNull { NoSuchElementException() }
