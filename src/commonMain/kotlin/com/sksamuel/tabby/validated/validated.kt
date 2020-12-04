@file:Suppress("unused")

package com.sksamuel.tabby.validated

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.left
import com.sksamuel.tabby.either.right

sealed class Validated<out E, out A> {

   data class Invalid<E>(val error: E) : Validated<E, Nothing>()
   data class Valid<A>(val value: A) : Validated<Nothing, A>()

   companion object {

      fun <A, B, R, ERROR> mapN(
         a: Validated<List<ERROR>, A>,
         b: Validated<List<ERROR>, B>,
         f: (A, B) -> R
      ): Validated<List<ERROR>, R> {
         val errors = listOf(a, b).filterIsInstance<Invalid<ERROR>>().map { it.error }
         return if (errors.isNotEmpty()) Invalid(errors) else {
            f(a.getUnsafe(), b.getUnsafe()).valid()
         }
      }

      fun <A, B, C, ERROR, R> mapN(
         a: Validated<ERROR, A>,
         b: Validated<ERROR, B>,
         c: Validated<ERROR, C>,
         f: (A, B, C) -> R
      ): Validated<List<ERROR>, R> {
         val errors = listOf(a, b, c).filterIsInstance<Invalid<ERROR>>().map { it.error }
         return if (errors.isNotEmpty()) Invalid(errors) else {
            f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe()).valid()
         }
      }

      inline fun <A, B, C, D, ERROR, R> mapN(
         a: Validated<ERROR, A>,
         b: Validated<ERROR, B>,
         c: Validated<ERROR, C>,
         d: Validated<ERROR, D>,
         f: (A, B, C, D) -> R
      ): Validated<List<ERROR>, R> {
         val errors = listOf(a, b, c, d).filterIsInstance<Invalid<ERROR>>().map { it.error }
         return if (errors.isNotEmpty()) Invalid(errors) else {
            f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe()).valid()
         }
      }

      inline fun <A, B, C, D, E, ERROR, R> mapN(
         a: Validated<ERROR, A>,
         b: Validated<ERROR, B>,
         c: Validated<ERROR, C>,
         d: Validated<ERROR, D>,
         e: Validated<ERROR, E>,
         f: (A, B, C, D, E) -> R
      ): Validated<List<ERROR>, R> {
         val errors = listOf(a, b, c, d, e).filterIsInstance<Invalid<ERROR>>().map { it.error }
         return if (errors.isNotEmpty()) Invalid(errors) else {
            f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe(), e.getUnsafe()).valid()
         }
      }

      inline fun <A, B, C, D, E, F, ERROR, R> mapN(
         a: Validated<ERROR, A>,
         b: Validated<ERROR, B>,
         c: Validated<ERROR, C>,
         d: Validated<ERROR, D>,
         e: Validated<ERROR, E>,
         f: Validated<ERROR, F>,
         fn: (A, B, C, D, E, F) -> R,
      ): Validated<List<ERROR>, R> {
         val errors = listOf(a, b, c, d, e, f).filterIsInstance<Invalid<ERROR>>().map { it.error }
         return if (errors.isNotEmpty()) Invalid(errors) else {
            fn(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe(), e.getUnsafe(), f.getUnsafe()).valid()
         }
      }

      inline fun <A, B, C, D, E, F, G, ERROR, R> mapN(
         a: Validated<ERROR, A>,
         b: Validated<ERROR, B>,
         c: Validated<ERROR, C>,
         d: Validated<ERROR, D>,
         e: Validated<ERROR, E>,
         f: Validated<ERROR, F>,
         g: Validated<ERROR, G>,
         fn: (A, B, C, D, E, F, G) -> R,
      ): Validated<List<ERROR>, R> {
         val errors = listOf(a, b, c, d, e, f, g).filterIsInstance<Invalid<ERROR>>().map { it.error }
         return if (errors.isNotEmpty()) Invalid(errors) else {
            fn(
               a.getUnsafe(),
               b.getUnsafe(),
               c.getUnsafe(),
               d.getUnsafe(),
               e.getUnsafe(),
               f.getUnsafe(),
               g.getUnsafe(),
            ).valid()
         }
      }

      inline fun <A, B, C, D, E, F, G, H, ERROR, R> mapN(
         a: Validated<ERROR, A>,
         b: Validated<ERROR, B>,
         c: Validated<ERROR, C>,
         d: Validated<ERROR, D>,
         e: Validated<ERROR, E>,
         f: Validated<ERROR, F>,
         g: Validated<ERROR, G>,
         h: Validated<ERROR, H>,
         fn: (A, B, C, D, E, F, G, H) -> R
      ): Validated<List<ERROR>, R> {
         val errors = listOf(a, b, c, d, e, f, g, h).filterIsInstance<Invalid<ERROR>>().map { it.error }
         return if (errors.isNotEmpty()) Invalid(errors) else {
            fn(
               a.getUnsafe(),
               b.getUnsafe(),
               c.getUnsafe(),
               d.getUnsafe(),
               e.getUnsafe(),
               f.getUnsafe(),
               g.getUnsafe(),
               h.getUnsafe(),
            ).valid()
         }
      }

      inline fun <A, B, C, D, E, F, G, H, I, ERROR, R> mapN(
         a: Validated<ERROR, A>,
         b: Validated<ERROR, B>,
         c: Validated<ERROR, C>,
         d: Validated<ERROR, D>,
         e: Validated<ERROR, E>,
         f: Validated<ERROR, F>,
         g: Validated<ERROR, G>,
         h: Validated<ERROR, H>,
         i: Validated<ERROR, I>,
         fn: (A, B, C, D, E, F, G, H, I) -> R
      ): Validated<List<ERROR>, R> {
         val errors = listOf(a, b, c, d, e, f, g, h, i).filterIsInstance<Invalid<ERROR>>().map { it.error }
         return if (errors.isNotEmpty()) Invalid(errors) else {
            fn(
               a.getUnsafe(),
               b.getUnsafe(),
               c.getUnsafe(),
               d.getUnsafe(),
               e.getUnsafe(),
               f.getUnsafe(),
               g.getUnsafe(),
               h.getUnsafe(),
               i.getUnsafe(),
            ).valid()
         }
      }
   }

   /**
    * Returns the value of this Validated if it is an instance of Valid.
    * Otherwise throws an [IllegalStateException].
    */
   fun getUnsafe(): A = when (this) {
      is Invalid -> throw IllegalStateException("Not a valid instance, was $this")
      is Valid -> this.value
   }

   /**
    * Converts this Validated to an Either.
    */
   fun toEither(): Either<E, A> = fold({ it.left() }, { it.right() })

   inline fun <F> mapInvalid(f: (E) -> F): Validated<F, A> = when (this) {
      is Invalid -> f(this.error).invalid()
      is Valid -> this
   }

   inline fun onValid(f: (A) -> Unit): Validated<E, A> {
      fold({}, { f(it) })
      return this
   }

   inline fun onInvalid(f: (E) -> Unit): Validated<E, A> = when (this) {
      is Valid -> this
      is Invalid -> {
         f(this.error)
         this
      }
   }

   inline fun <B> fold(ifInvalid: (E) -> B, ifValid: (A) -> B): B = when (this) {
      is Invalid -> ifInvalid(error)
      is Valid -> ifValid(value)
   }

   inline fun <B> map(f: (A) -> B): Validated<E, B> = when (this) {
      is Invalid -> this
      is Valid -> f(this.value).valid()
   }
}

inline fun <A, B, E> Validated<E, A>.flatMap(f: (A) -> Validated<E, B>): Validated<E, B> = when (this) {
   is Validated.Invalid<E> -> this
   is Validated.Valid -> f(this.value)
}

/**
 * Executes the given function, catching any exception. If the function
 * returns normally, then a Valid is returned, otherwise an Invalid
 * is returned with the exception.
 */
fun <A> validate(f: () -> A): Validated<Throwable, A> {
   return try {
      f().valid()
   } catch (e: Throwable) {
      e.invalid()
   }
}

fun <A, E> Validated<E, A>.filter(error: E, isValid: (A) -> Boolean) = fold(
   { this },
   { if (isValid(it)) it.valid() else error.invalid() }
)

/**
 * Executes the given test on the receiver. If the test returns true, then the value
 * is wrapped in a [Validated.Valid], otherwise the an Invalid with a generic error message
 * is returned.
 */
fun <A> A.validate(test: (A) -> Boolean): Validated<String, A> =
   if (test(this)) this.valid() else "$this is invalid".invalid()

/**
 * Executes the given test on the receiver. If the test returns true, then the value
 * is wrapped in a [Validated.Valid], otherwise the [ifInvalid] function is executed and
 * the result wrapped in an [Validated.Invalid].
 */
fun <A, E> A.validate(ifInvalid: E, test: (A) -> Boolean): Validated<E, A> =
   if (test(this)) this.valid() else ifInvalid.invalid()

fun <A> A.valid(): Validated<Nothing, A> = Validated.Valid(this)
fun <E> E.invalid(): Validated<E, Nothing> = Validated.Invalid(this)

/**
 * If all elements of the receiver list are [Validated.Valid] then executes the given function [f] with those values.
 * Otherwise returns the elements that failed as an [Validated.Invalid] containing a list of errors.
 */
inline fun <A, B, E> List<Validated<E, A>>.mapN(f: () -> B): Validated<List<E>, B> {
   val invalids = filterIsInstance<Validated.Invalid<E>>()
   return if (invalids.isEmpty()) f().valid() else invalids.map { it.error }.invalid()
}

fun <A, E> List<Validated<E, A>>.sequence(): Validated<List<E>, List<A>> {
   val invalids = filterIsInstance<Validated.Invalid<E>>()
   val valids = filterIsInstance<Validated.Valid<A>>()
   return if (invalids.isEmpty()) valids.map { it.value }.valid() else invalids.map { it.error }.invalid()
}
