package com.sksamuel.tabby.results

/**
 * If this [Result] is a failure, returns [other], otherwise returns this.
 */
fun <A> Result<A>.orElse(other: Result<A>): Result<A> = if (this.isFailure) other else this

inline fun <A> Result<A>.orElse(f: () -> Result<A>): Result<A> = if (this.isFailure) f() else this

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIf(p: (A) -> Boolean): Result<A> = failIf(RuntimeException("failure"), p)

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIf(message: String, p: (A) -> Boolean): Result<A> = failIf(RuntimeException(message), p)

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
@Deprecated("use", ReplaceWith("failIf(message, p)"))
fun <A> Result<A>.failIf(p: (A) -> Boolean, message: String): Result<A> = failIf(message, p)

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned, otherwise this is returned.
 */
@Deprecated("Use failIf(exception, p)", ReplaceWith("failIf(exception, p)"))
fun <A> Result<A>.failIf(p: (A) -> Boolean, exception: Exception): Result<A> = failIf(exception, p)

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns true,
 * then a failed Result is returned containing the given [exception], otherwise [this] is returned.
 */
inline fun <A> Result<A>.failIf(exception: Exception, p: (A) -> Boolean): Result<A> =
   flatMap { if (p(it)) exception.failure() else it.success() }

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIfNot(p: (A) -> Boolean) = failIfNot(p, RuntimeException("failure"))

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIfNot(p: (A) -> Boolean, message: String) = failIfNot(p, RuntimeException(message))

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIfNot(p: (A) -> Boolean, f: (A) -> Exception) =
   flatMap { if (!p(it)) f(it).failure() else it.success() }

/**
 * If this [Result] is a success, invokes the given predicate [p]. If the predicate returns false,
 * then a failed Result is returned, otherwise this is returned.
 */
inline fun <A> Result<A>.failIfNot(p: (A) -> Boolean, exception: Exception) =
   flatMap { if (!p(it)) exception.failure() else it.success() }

/**
 * If this [Result] is a success containing null, returns a failure with the given message.
 * Otherwise, returns the input.
 */
fun <A> Result<A?>.failIfNull(message: String): Result<A> = failIfNull { Exception(message) }

/**
 * If this [Result] is a success containing null, returns a failure with the given exception.
 * Otherwise, returns the input.
 */
inline fun <A> Result<A?>.failIfNull(fn: () -> Exception): Result<A> =
   flatMap { it?.success() ?: Result.failure(fn()) }

fun <A> Result<A?>.failIfNull() = failIfNull { NoSuchElementException() }

/**
 * If this [Result] is a success containing a non-null, returns a failure with the given message.
 * Otherwise, returns the input.
 */
fun <A> Result<A?>.failIfNotNull(message: String): Result<A> = failIfNotNull { Exception(message) }

/**
 * If this [Result] is a success containing a non-null, returns a failure with the given exception.
 * Otherwise, returns the input.
 */
inline fun <A> Result<A?>.failIfNotNull(fn: () -> Exception): Result<A> =
   flatMap { it?.success() ?: Result.failure(fn()) }

fun Result<Boolean>.failIfFalse(): Result<Unit> =
   flatMap { if (it) Result.unit() else Result.failure(NoSuchElementException()) }

fun Result<Boolean>.failIfFalse(fn: () -> Exception): Result<Unit> =
   flatMap { if (it) Result.unit() else Result.failure(fn()) }

/**
 * Returns a successful [Result] which contains Unit.
 */
fun Result.Companion.unit() = Unit.success()

fun <A> Result<A>.omit(): Result<Unit> = this.flatMap { Unit.success() }

fun <A> Result<Result<A>>.flatten(): Result<A> = this.fold({ it }, { it.failure() })

fun <A> Result<A>.exceptionOrThrow() = this.exceptionOrNull() ?: throw IllegalStateException("Expected exception")

fun <A> Result<A?>.onSuccessIfNotNull(f: (A) -> Unit) = this.onSuccess { if (it != null) f(it) }

fun <A> Result<A>.onEach(f: () -> Unit) = this.onSuccess { f() }.onFailure { f() }
