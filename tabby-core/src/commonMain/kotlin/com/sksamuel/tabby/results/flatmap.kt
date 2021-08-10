package com.sksamuel.tabby.results

inline fun <A, B> Result<A>.flatMap(other: (A) -> Result<B>): Result<B> =
   fold({ other(it) }, { Result.failure(it) })

