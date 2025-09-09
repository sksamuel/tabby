package com.sksamuel.tabby.results

fun Result<Boolean>.failIfFalse(): Result<Unit> =
   flatMap { if (it) Result.unit() else Result.failure(NoSuchElementException()) }

/**
 * If this [Result] is successful and contains the value false, returns a failed
 * result with the error given by the function [fn].
 */
fun Result<Boolean>.failIfFalse(fn: () -> Exception): Result<Unit> =
   flatMap { if (it) Result.unit() else Result.failure(fn()) }

/**
 * Returns the result of this [Result] if successful, otherwise returns false.
 */
fun Result<Boolean>.getOrFalse(): Boolean = getOrElse { false }


fun Result<Boolean>.failIfTrue(): Result<Unit> =
   flatMap { if (it) Result.unit() else Result.failure(NoSuchElementException()) }

/**
 * If this [Result] is successful and contains the value true, returns a failed
 * result with the error given by the function [fn].
 */
fun Result<Boolean>.failIfTrue(fn: () -> Exception): Result<Unit> =
   flatMap { if (it) Result.unit() else Result.failure(fn()) }

/**
 * Returns the result of this [Result] if successful, otherwise returns true.
 */
fun Result<Boolean>.getOrTrue(): Boolean = getOrElse { true }
