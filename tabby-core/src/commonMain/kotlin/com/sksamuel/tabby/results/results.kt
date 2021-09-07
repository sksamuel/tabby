package com.sksamuel.tabby.results

fun <A> A?.failureIfNull(message: String): Result<A> =
   if (this == null) Result.failure(RuntimeException(message)) else Result.success(this)
