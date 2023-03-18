package com.sksamuel.tabby.results

/**
 * If this [Result] is a failure, and the given predicate [p] evalutes to true,
 * then the result of the given function [f] is returned as a success,
 * otherwise the receiver is returned as is.
 */
fun <A> Result<A>.recoverIf(p: (Throwable) -> Boolean, f: (Throwable) -> A): Result<A> {
   return fold(
      { it.success() },
      { if (p(it)) f(it).success() else it.failure() }
   )
}

/**
 * If this [Result] is a failure, and the given predicate [p] evalutes to true,
 * then a nullable success is returned, otherwise the receiver is returned as is.
 */
fun <A> Result<A>.recoverNullIf(p: (Throwable) -> Boolean): Result<A?> {
   return fold(
      { it.success() },
      { if (p(it)) Result.success(null) else it.failure() }
   )
}
