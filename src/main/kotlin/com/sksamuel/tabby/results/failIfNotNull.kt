package com.sksamuel.tabby.results

/**
 * If this [Result] is a success containing a non-null, returns a failure with a [IllegalStateException].
 * Otherwise, returns the receiver.
 */
fun <A : Any> Result<A?>.failIfNotNull(): Result<A?> = failIfNotNull { IllegalStateException() }

/**
 * If this [Result] is a success containing a non-null, returns a failure with the given message.
 * Otherwise, returns the receiver.
 */
fun <A : Any> Result<A?>.failIfNotNull(message: String): Result<A?> = failIfNotNull { Exception(message) }

/**
 * If this [Result] is a success containing a non-null, returns a failure with the given exception.
 * Otherwise, returns the receiver.
 */
inline fun <A : Any> Result<A?>.failIfNotNull(fn: () -> Exception): Result<A?> =
   flatMap { if (it == null) Result.success(null) else fn().failure() }
