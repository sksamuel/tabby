package com.sksamuel.tabby.results

fun <A, B> Result<A>.flatMap(other: (A) -> Result<B>): Result<B> =
   fold({ other(it) }, { Result.failure(it) })

