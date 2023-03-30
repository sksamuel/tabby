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
 * If this [Result] is a success that contains null, will replace the null with the
 * value of the function [f]. Otherwise, returns as is.
 */
inline fun <A> Result<A?>.replaceNull(f: () -> A): Result<A> {
   return this.fold({ it?.success() ?: f().success() }, { it.failure() })
}

inline fun <A> Result<A?>.onFailureOrNull(f: () -> Unit): Result<A?> {
   return this.fold(
      {
         if (it == null) f()
         it.success()
      },
      {
         f()
         it.failure()
      }
   )
}
