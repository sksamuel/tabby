package com.sksamuel.tabby

sealed class Validated<out E, out A> {
   data class Invalid<E>(val error: E) : Validated<E, Nothing>()
   data class Valid<A>(val value: A) : Validated<Nothing, A>()

   companion object {
      fun <A> catch(f: () -> A): Validated<String, A> {
         return try {
            f().valid()
         } catch (e: Exception) {
            e.message!!.invalid()
         }
      }
   }

   inline fun getUnsafe(): A = when (this) {
      is Invalid -> throw IllegalStateException("Not a valid instance, was $this")
      is Valid -> this.value
   }

   inline fun toEither(): Either<E, A> = fold({ it.left() }, { it.right() })

   inline fun <F> mapInvalid(f: (E) -> F): Validated<F, A> = when (this) {
      is Validated.Invalid -> f(this.error).invalid()
      is Validated.Valid -> this
   }

   inline fun onValid(f: (A) -> Unit): Validated<E, A> {
      fold({}, { f(it) })
      return this
   }
}

fun <A, E> A.validate(ifInvalid: E, test: (A) -> Boolean): Validated<E, A> =
   if (test(this)) this.valid() else ifInvalid.invalid()

fun <E, A> Validated<E, A>.filter(error: E, isValid: (A) -> Boolean) = fold(
   { this },
   { if (isValid(it)) it.valid() else error.invalid() }
)

fun <A> A.valid(): Validated<Nothing, A> = Validated.Valid(this)
fun <E> E.invalid(): Validated<E, Nothing> = Validated.Invalid(this)

inline fun <E, A, B> Validated<E, A>.map(f: (A) -> B) = when (this) {
   is Validated.Invalid -> this
   is Validated.Valid -> f(this.value).valid()
}

inline fun <E, A, B> Validated<E, A>.flatMap(f: (A) -> Validated<E, B>) = when (this) {
   is Validated.Invalid -> this
   is Validated.Valid -> f(this.value)
}

inline fun <A, E, T> Validated<E, A>.fold(ifInvalid: (E) -> T, ifValid: (A) -> T): T = when (this) {
   is Validated.Invalid -> ifInvalid(error)
   is Validated.Valid -> ifValid(value)
}

inline fun <A, E> Validated<E, A>.onValid(f: (A) -> Unit): Validated<E, A> = when (this) {
   is Validated.Invalid -> this
   is Validated.Valid -> {
      f(this.value)
      this
   }
}

inline fun <A, E> Validated<E, A>.onInvalid(f: (E) -> Unit): Validated<E, A> = when (this) {
   is Validated.Valid -> this
   is Validated.Invalid -> {
      f(this.error)
      this
   }
}

inline fun <A, E, F> Validated<E, A>.mapInvalid(f: (E) -> F): Validated<F, A> = when (this) {
   is Validated.Invalid -> f(error).invalid()
   is Validated.Valid -> this
}

fun <A, B, C, E> applicative(a: Validated<List<E>, A>,
                             b: Validated<List<E>, B>,
                             f: (A, B) -> C): Validated<List<E>, C> {
   return when (a) {
      is Validated.Invalid -> when (b) {
         is Validated.Invalid -> (a.error + b.error).invalid()
         is Validated.Valid -> a
      }
      is Validated.Valid -> when (b) {
         is Validated.Invalid -> b
         is Validated.Valid -> f(a.value, b.value).valid()
      }
   }
}

fun <A, B, C, R, E> applicative(a: Validated<E, A>,
                                b: Validated<E, B>,
                                c: Validated<E, C>,
                                f: (A, B, C) -> R): Validated<List<E>, R> {
   val errors = listOf(a, b, c).filterIsInstance<Validated.Invalid<E>>().map { it.error }
   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe()).valid()
   }
}

inline fun <A, B, C, D, E, R> applicative(a: Validated<E, A>,
                                          b: Validated<E, B>,
                                          c: Validated<E, C>,
                                          d: Validated<E, D>,
                                          f: (A, B, C, D) -> R): Validated<List<E>, R> {
   val errors = listOf(a, b, c, d).filterIsInstance<Validated.Invalid<E>>().map { it.error }
   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe()).valid()
   }
}

inline fun <A, B, C, D, E, G, H, I, J, Error, R> applicative(a: Validated<Error, A>,
                                                             b: Validated<Error, B>,
                                                             c: Validated<Error, C>,
                                                             d: Validated<Error, D>,
                                                             e: Validated<Error, E>,
                                                             g: Validated<Error, G>,
                                                             h: Validated<Error, H>,
                                                             i: Validated<Error, I>,
                                                             j: Validated<Error, J>,
                                                             f: (A, B, C, D, E, G, H, I, J) -> R): Validated<List<Error>, R> {
   val errors = listOf(a, b, c, d, e, g, h, i, j).filterIsInstance<Validated.Invalid<Error>>().map { it.error }
   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe(), e.getUnsafe(), g.getUnsafe(), h.getUnsafe(), i.getUnsafe(), j.getUnsafe()).valid()
   }
}

inline fun <A, B, E> applicative(vs: List<Validated<E, A>>, f: () -> B): Validated<List<E>, B> {
   val invalids = vs.filterIsInstance<Validated.Invalid<E>>()
   return if (invalids.isEmpty()) f().valid() else invalids.map { it.error }.invalid()
}

fun <A, E> List<Validated<E, A>>.sequence(): Validated<List<E>, List<A>> {
   val invalids = filterIsInstance<Validated.Invalid<E>>()
   val valids = filterIsInstance<Validated.Valid<A>>()
   return if (invalids.isEmpty()) valids.map { it.value }.valid() else invalids.map { it.error }.invalid()
}
