package com.sksamuel.tabby.results

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIfNot(p: (A) -> Boolean) =
   failIfNot({ RuntimeException("failure") }, p)

inline fun <A> Result<A>.failIfNot(message: String, p: (A) -> Boolean) =
   failIfNot({ RuntimeException(message) }, p)

inline fun <A> Result<A>.failIfNot(exceptionFn: (A) -> Exception, p: (A) -> Boolean) =
   flatMap { if (!p(it)) exceptionFn(it).failure() else it.success() }

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
@Deprecated("use failIfNot(message, p)")
@JvmName("failIfNotMessage")
inline fun <A> Result<A>.failIfNot(p: (A) -> Boolean, message: String) =
   failIfNot(p, RuntimeException(message))

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
@Deprecated("use failIfNot(exceptionFn, p)")
@JvmName("failIfNotExceptionFn")
inline fun <A> Result<A>.failIfNot(p: (A) -> Boolean, f: (A) -> Exception) =
   flatMap { if (!p(it)) f(it).failure() else it.success() }

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
@Deprecated("use failIfNot(exceptionFn, p)")
@JvmName("failIfNotException")
inline fun <A> Result<A>.failIfNot(p: (A) -> Boolean, exception: Exception) =
   flatMap { if (!p(it)) exception.failure() else it.success() }
