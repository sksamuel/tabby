package com.sksamuel.tabby.validation

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.left
import com.sksamuel.tabby.either.right
import kotlin.reflect.KFunction1

sealed class Validated<out E, out A> {

   data class Invalid<E>(val errors: List<E>) : Validated<E, Nothing>()
   data class Valid<A>(val value: A) : Validated<Nothing, A>()

   companion object {

   }

   inline fun <B> map(f: (A) -> B): Validated<E, B> = when (this) {
      is Valid -> f(this.value).valid()
      is Invalid -> this
   }

   /**
    * Returns the value of this Validated if it is an instance of Valid.
    * Otherwise throws an [IllegalStateException].
    */
   fun getUnsafe(): A = when (this) {
      is Invalid -> throw IllegalStateException("Not a valid instance, was $this")
      is Valid -> this.value
   }

   inline fun <B> fold(ifInvalid: (List<E>) -> B, ifValid: (A) -> B): B = when (this) {
      is Invalid -> ifInvalid(errors)
      is Valid -> ifValid(value)
   }

   inline fun onValid(f: (A) -> Unit): Validated<E, A> {
      fold({}, { f(it) })
      return this
   }

   inline fun onInvalid(f: (List<E>) -> Unit): Validated<E, A> {
      if (this is Invalid<E>) f(this.errors)
      return this
   }

   /**
    * Converts this Validated to an Either.
    */
   fun toEither(): Either<List<E>, A> = fold({ it.left() }, { it.right() })
}

fun <I, A, E> Validated.Companion.repeated(f: (I) -> Validated<E, A>, inputs: List<I>): Validated<E, List<A>> {
   return inputs.map { f(it) }.traverse()
}

inline fun <A, B, E> Validated<E, A>.flatMap(f: (A) -> Validated<E, B>): Validated<E, B> = when (this) {
   is Validated.Valid -> f(this.value)
   is Validated.Invalid -> this
}

private fun <I, E, O> KFunction1<I, Validated<E, O>>.multiple(input: List<I>): Validated<E, List<O>> {
   return input.map { this@multiple.invoke(it) }.traverse()
}

fun <ERROR, A> List<Validated<ERROR, A>>.traverse(): Validated<ERROR, List<A>> {
   val errors = this.filterIsInstance<Validated.Invalid<ERROR>>().flatMap { it.errors }
   return if (errors.isNotEmpty()) errors.invalid() else this.map { it.getUnsafe() }.valid()
}


fun <A> A.valid(): Validated<Nothing, A> = Validated.Valid(this)
fun <E> E.invalid(): Validated<E, Nothing> = Validated.Invalid(listOf(this))
fun <E> List<E>.invalid(): Validated<E, Nothing> = Validated.Invalid(this)
