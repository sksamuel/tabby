package com.sksamuel.tabby.option

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.validated.Validated
import com.sksamuel.tabby.validated.invalid
import com.sksamuel.tabby.io.IO
import com.sksamuel.tabby.io.failure
import com.sksamuel.tabby.io.success
import com.sksamuel.tabby.either.left
import com.sksamuel.tabby.either.right
import com.sksamuel.tabby.validated.valid
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

sealed class Option<out A> {

   data class Some<A>(val value: A) : Option<A>()
   object None : Option<Nothing>()

   companion object {
      /**
       * Wraps a nullable value in an Option. If the value is null, then a [None] is returned,
       * otherwise a [Some] is returned that contains the value.
       */
      operator fun <T> invoke(t: T?): Option<T> = t?.some() ?: None
   }

   /**
    * If a [Some], executes the given side effecting function [f] with the value of this option.
    *
    * @return this
    */
   inline fun forEach(f: (A) -> Unit): Option<A> {
      when (this) {
         is Some -> f(value)
         is None -> Unit
      }
      return this
   }

   /**
    * If a [None], executes the given side effecting function [f].
    *
    * @return this
    */
   inline fun onNone(f: () -> Unit): Option<A> = fold({ f(); none }, { it.some() })

   /**
    * If a [Some], executes the given function [f] with the value of this option, returning
    * a new option wrapping the return value of the function. Otherwise returns [None].
    *
    * @return a new option wrapping function f or none.
    */
   inline fun <B> map(f: (A) -> B): Option<B> = if (isEmpty()) none else Some(f(this.getUnsafe()))

   inline fun <B> flatMap(f: (A) -> Option<B>): Option<B> = when (this) {
      is Some -> f(this.value)
      else -> None
   }

   inline fun <B> fold(ifEmpty: () -> B, ifDefined: (A) -> B): B = when (this) {
      is Some -> ifDefined(this.value)
      else -> ifEmpty()
   }

   inline fun <B> fold(ifEmpty: B, ifDefined: (A) -> B): B = when (this) {
      is Some -> ifDefined(this.value)
      else -> ifEmpty
   }

   /**
    * Returns the value of this Option if it is a Some, otherwise returns null.
    */
   fun orNull(): A? = when (this) {
      is Some -> this.value
      else -> null
   }

   /**
    * Returns this option if it is nonempty and applying the predicate [p] to
    * this option's value returns true. Otherwise, returns none.
    */
   fun filter(p: (A) -> Boolean): Option<A> = if (isEmpty() || p(this.getUnsafe())) this else none

   /**
    * Returns this option if it is nonempty and applying the predicate [p] to
    * this option's value returns true. Otherwise, returns none.
    */
   fun filterNot(p: (A) -> Boolean): Option<A> = if (isEmpty() || !p(this.getUnsafe())) this else none

   /**
    * Returns an [Either.Right] containing this option's value if it is non empty, otherwise returns
    * a [Either.Left] containing the result of the function [left].
    */
   fun <X> toRight(left: () -> X): Either<X, A> = if (isEmpty()) Either.Left(left()) else Either.Right(this.getUnsafe())

   /**
    * Returns an [Either.Left] containing this option's value if it is non empty, otherwise returns
    * a [Either.Right] containing the result of the function [right].
    */
   fun <X> toLeft(right: () -> X): Either<A, X> =
      if (isEmpty()) Either.Right(right()) else Either.Left(this.getUnsafe())

   /**
    * @return true if this is a [Some] and false if a [None].
    */
   fun isDefined(): Boolean = !isEmpty()

   /**
    * @return true if this is a [None] and false if a [Some].
    */
   fun isEmpty(): Boolean = this is None

   /**
    * @return true if this is a [Some] and the value of the predicate [p] evaluates to true given the contained
    * value of the option, otherwise false.
    */
   fun exists(p: (A) -> Boolean) = fold(false, p)

   /**
    * Transforms this option into a list.
    *
    * @return if this is a [None], returns an empty list, otherwise returns a list containing the value of this option.
    */
   fun toList(): List<A> = fold(emptyList(), { listOf(it) })

   fun <B, C> zip(other: Option<B>): Option<Pair<A, B>> = when (this) {
      is Some -> when (other) {
         is Some -> Pair(this.value, other.value).some()
         else -> None
      }
      else -> None
   }

   fun <B, C> mapN(other: Option<B>, f: (A, B) -> C): Option<C> = when (this) {
      is Some -> when (other) {
         is Some -> f(this.value, other.value).some()
         else -> None
      }
      else -> None
   }

   fun <B, C, R> mapN(b: Option<B>, c: Option<C>, f: (A, B, C) -> R): Option<R> = when (this) {
      is Some -> when (b) {
         is Some -> when (c) {
            is Some -> f(this.value, b.value, c.value).some()
            else -> None
         }
         else -> None
      }
      else -> None
   }

   /**
    * Returns the value of this option or throws.
    *
    * @return if a [Some] returns the contained value, otherwise throws an [IllegalStateException].
    */
   fun getUnsafe(): A = fold({ throw IllegalStateException() }, { it })

   /**
    * If a [Some] returns an [Either.Right] containing the value of this option. Otherwise returns
    * an [Either.Left] with the value of the function [ifEmpty].
    *
    * @return an either containing the value of this option or the value of the given function [ifEmpty].
    */
   fun <B> toEither(ifEmpty: () -> B): Either<B, A> = fold({ ifEmpty().left() }, { it.right() })

   fun toTry(message: String): Try<A> = fold({ RuntimeException(message).left() }, { it.right() })

   /**
    * Transforms this option into a [Validated].
    *
    * @return if this is a [None], returns an [Validated.Invalid] with the result of the given
    * function [isEmpty], otherwise returns a [Validated.Valid] with the value of the [Some].
    */
   fun <E> toValidated(isEmpty: () -> E): Validated<E, A> = fold({ isEmpty().invalid() }, { it.valid() })

   fun <E> fail(f: () -> E): IO<E, A> = fold({ f().failure() }, { it.success() })
}

fun <A> Option<A>.contains(a: A) = fold(false, { it == a })

fun <A> Option<A>.orElse(other: Option<A>): Option<A> = fold({ other }, { it.some() })

@JvmName("orElseFn")
fun <A> Option<A>.orElse(f: () -> Option<A>): Option<A> = fold({ f() }, { it.some() })

fun <A> Option<A>.getOrElse(a: A): A = when (this) {
   is Option.None -> a
   is Option.Some -> this.value
}

inline fun <A> Option<A>.getOrElse(f: () -> A): A = when (this) {
   is Option.None -> f()
   is Option.Some -> this.value
}

@Deprecated("use mapN")
fun <A, B, R> applicative(a: Option<A>, b: Option<B>, f: (A, B) -> R): Option<R> {
   return when (a) {
      is Option.Some -> when (b) {
         is Option.Some -> f(a.value, b.value).some()
         Option.None -> Option.None
      }
      Option.None -> Option.None
   }
}

@Deprecated("use mapN")
fun <A, B, C, D, R> applicative(
   a: Option<A>,
   b: Option<B>,
   c: Option<C>,
   d: Option<D>,
   f: (A, B, C, D) -> R,
): Option<R> {
   if (a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) return Option.None
   return f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe()).some()
}

val none = Option.None

fun <T> T.some(): Option<T> = if (this == null) error("Cannot use .some on a null") else Option.Some(this)

fun <T> T?.toOption(): Option<T> = this?.some() ?: Option.None

/**
 * For an Option of an Option, removees the inner option. If the receiver is a Some(Some(a)), returns Some(a),
 * otherwise returns None.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Option<Option<T>>.flatten(): Option<T> = when (this) {
   is Option.Some<*> -> when (this.value) {
      is Option.Some<*> -> this.value as Option.Some<T>
      else -> Option.None
   }
   else -> Option.None
}


