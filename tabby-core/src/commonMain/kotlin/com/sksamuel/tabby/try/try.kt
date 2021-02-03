@file:Suppress("unused")

package com.sksamuel.tabby.`try`

import com.sksamuel.tabby.effects.IO
import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.none
import com.sksamuel.tabby.option.some

sealed class Try<out A> {

   abstract val isSuccess: Boolean
   abstract val isFailure: Boolean

   companion object {

      operator fun <B> invoke(f: () -> B): Try<B> = catch(f)

      fun <A> success(a: A) = Success(a)
      fun failure(t: Throwable) = Failure(t)
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

   inline fun <B> flatMap(f: (A) -> Try<B>): Try<B> = when (this) {
      is Failure -> this
      is Success -> f(value)
   }

   inline fun getErrorOrElse(default: (A) -> Throwable): Throwable =
      fold({ it }, { default(it) })

   fun getValueOrNull(): A? = fold({ null }, { it })

   fun getErrorOrNull(): Throwable? = fold({ it }, { null })

   inline fun <B> bimap(isFailure: (Throwable) -> Throwable, ifSuccess: (A) -> B): Try<B> =
      fold({ isFailure(it).error() }, { ifSuccess(it).value() })

   // process the given function if this is an error, and then return this
   inline fun onFailure(f: (Throwable) -> Unit): Try<A> =
      fold({ f(it); this }, { this })

   inline fun onSuccess(f: (A) -> Unit): Try<A> =
      fold({ this }, { f(it); this })

   fun getValueUnsafe(): A = fold({ throw RuntimeException("Expected success but was $it") }, { it })

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

inline fun <A> Try<A>.orElse(other: () -> Try<A>): Try<A> {
   return fold({ other() }, { it.value() })
}

inline fun <A> Try<A>.getValueOrElse(default: (Throwable) -> A): A =
   fold({ default(it) }, { it })

/**
 * Invokes the given function [f] wrapping the result into a [Try.Success], or, if an exception
 * is thrown, will wrap the throwable into an [Try.Failure].
 */
inline fun <A> catch(f: () -> A): Try<A> = try {
   f().value()
} catch (t: Throwable) {
   t.error()
}

fun <A> Try<Try<A>>.flatten(): Try<A> = when (this) {
   is Try.Success -> this.value
   is Try.Failure -> this
}

fun <A> Try<A>.toIO(): IO<A> = IO.from(this.fold({ Either.Left(it) }, { Either.Right(it) }))

/**
 * Splits a list of [Try] into a pair of lists, one containing failures and one containing successes.
 */
fun <A> List<Try<A>>.split(): Pair<List<Throwable>, List<A>> {
   val (failures, successes) = this.partition { it is Try.Failure }
   return failures.map { it.getErrorUnsafe() } to successes.map { it.getValueUnsafe() }
}

fun Throwable.error() = Try.Failure(this)
fun <B> B.value() = Try.Success(this)
