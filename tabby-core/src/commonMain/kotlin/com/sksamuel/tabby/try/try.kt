@file:Suppress("unused")

package com.sksamuel.tabby.`try`

import com.sksamuel.tabby.effects.IO
import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.left
import com.sksamuel.tabby.either.right
import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.none
import com.sksamuel.tabby.option.some
import com.sksamuel.tabby.validated.Validated
import com.sksamuel.tabby.validated.invalid
import com.sksamuel.tabby.validated.valid

sealed class Try<out A> {

   abstract val isSuccess: Boolean
   abstract val isFailure: Boolean

   companion object {

      operator fun <A> invoke(f: () -> A): Try<A> = catch { f() }

      /**
       * Wraps a value in a success but returns the type signature Try.
       * Very useful for lifting values for use in folds.
       */
      operator fun <A> invoke(a: A): Try<A> = success(a)

      fun <A> success(a: A): Success<A> = Success(a)

      fun failure(t: Throwable): Failure = Failure(t)

      /**
       * Creates a failed Try by first wrapping this message into an [Exception].
       */
      fun failure(message: String) = Failure(Exception(message))

      val unit: Success<Unit> = Success(Unit)
   }

   data class Failure(val error: Throwable) : Try<Nothing>() {
      override val isSuccess: Boolean = false
      override val isFailure: Boolean = true
   }

   data class Success<A>(val value: A) : Try<A>() {
      override val isSuccess: Boolean = true
      override val isFailure: Boolean = false
   }

   inline fun mapError(f: (Throwable) -> Throwable): Try<A> =
      when (this) {
         is Failure -> Failure(f(this.error))
         is Success -> this
      }

   inline fun <B> map(f: (A) -> B): Try<B> =
      when (this) {
         is Failure -> this
         is Success -> Success(f(this.value))
      }

   inline fun <B> fold(ifFailure: (Throwable) -> B, ifSuccess: (A) -> B): B = when (this) {
      is Failure -> ifFailure(error)
      is Success -> ifSuccess(value)
   }

   @OverloadResolutionByLambdaReturnType
   inline fun <B> flatMap(f: (A) -> Try<B>): Try<B> = when (this) {
      is Failure -> this
      is Success -> f(value)
   }

   @OverloadResolutionByLambdaReturnType
   inline fun <B> flatMap(f: (A) -> IO<B>): IO<B> = when (this) {
      is Failure -> IO.failure(this.error)
      is Success -> f(this.value)
   }

   inline fun getErrorOrElse(default: (A) -> Throwable): Throwable =
      fold({ it }, { default(it) })

   fun getValueOrNull(): A? = fold({ null }, { it })

   fun getErrorOrNull(): Throwable? = fold({ it }, { null })

   fun toEither(): Either<Throwable, A> = fold({ it.left() }, { it.right() })

   inline fun <B> bimap(isFailure: (Throwable) -> Throwable, ifSuccess: (A) -> B): Try<B> =
      fold({ isFailure(it).failure() }, { ifSuccess(it).success() })

   // process the given function if this is an error, and then return this
   inline fun onFailure(f: (Throwable) -> Unit): Try<A> =
      fold({ f(it); this }, { this })

   inline fun onComplete(onFailure: (Throwable) -> Unit, onSuccess: (A) -> Unit): Try<A> =
      onFailure(onFailure).onSuccess(onSuccess)

   inline fun onError(f: (Throwable) -> Unit): Try<A> = onFailure(f)

   inline fun onSuccess(f: (A) -> Unit): Try<A> =
      fold({ this }, { f(it); this })

   /**
    * Invoked when we always expect the value to be present and want to short circuit if not.
    * The error indicates that a success was expected.
    *
    * If an error can reasonably occur, then use getValueOrThrow which will not wrap the
    * error in another exception.
    */
   fun getValueUnsafe(): A = fold({ throw RuntimeException("Expected success but was $it") }, { it })

   /**
    * Invoked to retrieve the value if successful or throw the error if a failure.
    */
   fun getValueOrThrow(): A = fold({ throw it }, { it })

   fun getErrorUnsafe(): Throwable = fold({ it }, { throw RuntimeException("Expected failure but was $it") })

   fun onEach(ifFailure: (Throwable) -> Unit, ifSuccess: (A) -> Unit): Try<A> {
      when (this) {
         is Failure -> ifFailure(error)
         is Success -> ifSuccess(value)
      }
      return this
   }

   fun toOption(): Option<A> = fold({ none }, { it.some() })
}

fun <A, R> Try<Option<A>>.trifold(ifError: (Throwable) -> R, ifEmpty: () -> R, ifValue: (A) -> R) {
   return fold(
      { ifError(it) },
      { option ->
         option.fold(
            { ifEmpty() },
            { ifValue(it) }
         )
      }
   )
}

/**
 * If this Try contains a null, will return a [Try.Failure] containing the error generated
 * from the given function [f]. Otherwise returns a [Try.Success] with the non-null value.
 */
fun <A> Try<A?>.absolve(f: () -> Throwable): Try<A> =
   fold(
      { it.failure() },
      { it?.success() ?: f().failure() }
   )

fun <A> Try<A>.recover(f: (Throwable) -> A): A = fold({ f(it) }, { it })

fun <A> A?.failureIfNull(f: () -> Throwable): Try<A> = this?.success() ?: f().failure()

fun <A> A.failureIf(isError: (A) -> Boolean, onError: () -> Throwable): Try<A> =
   if (isError(this)) onError().failure() else this.success()

inline fun <A> Try<A>.orElse(other: () -> Try<A>): Try<A> {
   return fold({ other() }, { it.success() })
}

inline fun <A> Try<A>.getValueOrElse(orElse: (Throwable) -> A): A =
   fold({ orElse(it) }, { it })


fun <A> Try<Try<A>>.flatten(): Try<A> = when (this) {
   is Try.Success -> this.value
   is Try.Failure -> this
}

fun <A> Try<A>.toIO(): IO<A> = IO.from(this)

/**
 * Splits a list of [Try] into a pair of lists, one containing failures and one containing successes.
 */
fun <A> List<Try<A>>.split(): Pair<List<Throwable>, List<A>> {
   val (failures, successes) = this.partition { it is Try.Failure }
   return failures.map { it.getErrorUnsafe() } to successes.map { it.getValueUnsafe() }
}

fun String.error() = Try.Failure(RuntimeException(this))

@Deprecated("use .failure()", ReplaceWith("Try.Failure(this)"))
fun Throwable.error() = Try.Failure(this)
fun Throwable.failure() = Try.Failure(this)

@Deprecated("Use .success()", ReplaceWith("Try.Success(this)"))
fun <B> B.value() = Try.Success(this)
fun <B> B.success() = Try.Success(this)

fun <A> Try<A>.toValidated(): Validated<Throwable, A> = fold({ it.invalid() }, { it.valid() })

inline fun <A> Try<A?>.mapIfNull(f: () -> A): Try<A> = map { it ?: f() }
inline fun <A> Try<A?>.flatMapIfNull(f: () -> Try<A>): Try<A> = flatMap { it?.success() ?: f() }

inline fun <A, B> Try<A?>.flatMapIfNotNull(f: (A) -> Try<B>): Try<B?> =
   flatMap { if (it == null) Try.success(null) else f(it) }

inline fun <A, B> Try<A?>.mapIfNotNull(f: (A) -> B): Try<B?> =
   map { if (it == null) null else f(it) }
