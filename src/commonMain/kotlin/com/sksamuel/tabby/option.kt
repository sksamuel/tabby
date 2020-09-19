package com.sksamuel.tabby

import kotlin.js.JsName

sealed class Option<out A> : Optional<A> {

   data class Some<A>(val value: A) : Option<A>()
   object None : Option<Nothing>()

   companion object {
      /**
       * Wraps a nullable value in an Option. If the value is null, then a [None] is returned,
       * otherwise a [Some] is returned that contains the value.
       */
      operator fun <T> invoke(t: T?): Option<T> = t?.some() ?: None
   }

   override fun toOption(): Option<A> = this

   /**
    * If a [Some], executes the given side effecting function [f] with the value of this option.
    *
    * @return this
    */
   inline fun forEach(f: (A) -> Unit): Option<A> {
      when (this) {
         is Some -> f(value)
         is None -> {
         }
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

   inline fun <B> flatMap(f: (A) -> Optional<B>): Option<B> = when (this) {
      is Some -> f(this.value).toOption()
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
   fun <X> toLeft(right: () -> X): Either<A, X> = if (isEmpty()) Either.Right(right()) else Either.Left(this.getUnsafe())

   fun isDefined(): Boolean = !isEmpty()
   fun isEmpty(): Boolean = this is None

   fun exists(p: (A) -> Boolean) = fold(false, p)

   fun toList(): List<A> = fold(emptyList(), { listOf(it) })

   fun <B> toEither(ifEmpty: () -> B): Either<B, A> = fold({ ifEmpty().left() }, { it.right() })

   fun <B, C> combine(other: Option<B>, f: (A, B) -> C): Option<C> = when (this) {
      is Some -> when (other) {
         is Some -> f(this.value, other.value).some()
         else -> None
      }
      else -> None
   }

   fun getUnsafe(): A = fold({ throw IllegalStateException() }, { it })

   fun <E> toValidated(isEmpty: () -> E): Validated<E, A> = fold({ isEmpty().invalid() }, { it.valid() })
}

fun <A> Option<A>.orElse(other: Option<A>): Option<A> = fold({ other }, { it.some() })

fun <A> Option<A>.getOrElse(a: A): A = when (this) {
   is Option.None -> a
   is Option.Some -> this.value
}

inline fun <A> Option<A>.getOrElse(f: () -> A): A = when (this) {
   is Option.None -> f()
   is Option.Some -> this.value
}

fun <A, B, R> applicative(a: Option<A>, b: Option<B>, f: (A, B) -> R): Option<R> {
   return when (a) {
      is Option.Some -> when (b) {
         is Option.Some -> f(a.value, b.value).some()
         Option.None -> Option.None
      }
      Option.None -> Option.None
   }
}

fun <A, B, C, D, R> applicative(a: Option<A>,
                                b: Option<B>,
                                c: Option<C>,
                                d: Option<D>,
                                f: (A, B, C, D) -> R): Option<R> {
   if (a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) return Option.None
   return f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe()).some()
}

@JsName("nonefun")
fun none() = Option.None
val none = Option.None

fun <T> T.some(): Option<T> = Option.Some(this)

fun <T> T?.toOption(): Option<T> = this?.some() ?: Option.None

/**
 * Returns the first element of this List wrapped in a Some if the list is non empty,
 * otherwise returns None.
 */
fun <A> List<A>.firstOrNone(): Option<A> = this.firstOrNull().toOption()

@Deprecated("Use flatMap with Optional")
inline fun <T, U : Any> List<T>.flatMapOption(f: (T) -> Option<U>): List<U> = mapNotNull { f(it).orNull() }

inline fun <T, U : Any> List<T>.flatMap(f: (T) -> Optional<U>): List<U> = mapNotNull { f(it).toOption().orNull() }

fun <A : Any> List<Option<A>>.flatten() = mapNotNull { it.orNull() }

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


