package com.sksamuel.tabby.results

fun Result<Boolean>.failIfFalse(): Result<Unit> =
   flatMap { if (it) Result.unit() else Result.failure(NoSuchElementException()) }

fun Result<Boolean>.failIfFalse(fn: () -> Exception): Result<Unit> =
   flatMap { if (it) Result.unit() else Result.failure(fn()) }

fun Result<Boolean>.getOrFalse(): Boolean = getOrElse { false }
