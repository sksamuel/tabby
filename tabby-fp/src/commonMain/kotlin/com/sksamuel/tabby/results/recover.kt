package com.sksamuel.tabby.results

fun <A> Result<A>.recoverIf(p: (Throwable) -> Boolean, fn: (Throwable) -> A): Result<A> {
   return fold(
      { it.success() },
      { if (p(it)) fn(it).success() else it.failure() }
   )
}
