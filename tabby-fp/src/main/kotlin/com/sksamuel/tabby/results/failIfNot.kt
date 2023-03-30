package com.sksamuel.tabby.results

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIfNot(p: (A) -> Boolean) =
   failIfNot({ RuntimeException() }, p)

inline fun <A> Result<A>.failIfNot(message: String, p: (A) -> Boolean) =
   failIfNot({ RuntimeException(message) }, p)

inline fun <A> Result<A>.failIfNot(exceptionFn: (A) -> Exception, p: (A) -> Boolean) =
   flatMap { if (!p(it)) exceptionFn(it).failure() else it.success() }
