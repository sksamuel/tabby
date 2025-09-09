package com.sksamuel.tabby.results

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIf(p: (A) -> Boolean): Result<A> =
   failIf(RuntimeException("failure"), p)

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIf(message: String, p: (A) -> Boolean): Result<A> =
   failIf(RuntimeException(message), p)

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIf(exceptionFn: (A) -> Exception, p: (A) -> Boolean): Result<A> =
   flatMap { if (p(it)) exceptionFn(it).failure() else it.success() }

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
@Deprecated("use", ReplaceWith("failIf(message, p)"))
fun <A> Result<A>.failIf(p: (A) -> Boolean, message: String): Result<A> =
   failIf(message, p)

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
@Deprecated("Use failIf(exception, p)", ReplaceWith("failIf(exception, p)"))
fun <A> Result<A>.failIf(p: (A) -> Boolean, exception: Exception): Result<A> =
   failIf(exception, p)

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned containing the given [exception], otherwise [this] is returned.
 */
@Deprecated("Use failIf(exceptionFn, p)", ReplaceWith("failIf(exception, p)"))
inline fun <A> Result<A>.failIf(exception: Exception, p: (A) -> Boolean): Result<A> =
   flatMap { if (p(it)) exception.failure() else it.success() }
