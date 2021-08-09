@file:Suppress("unused")

package com.sksamuel.tabby.either

import com.sksamuel.tabby.`try`.failure
import com.sksamuel.tabby.`try`.success
import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.none
import com.sksamuel.tabby.option.some
import com.sksamuel.tabby.validated.Validated
import com.sksamuel.tabby.validated.invalid
import com.sksamuel.tabby.validated.valid

/**
 * An internal, non biased implementation of Either.
 *
 * This class is similar to Arrow's Either (which in turn is based on Scala's) but allows
 * for non-effect based handling.
 *
 * The terminology deliberately departs from the terminology used by other FP libraries
 * in order to make the learning curve shallower for those who are from a non-FP background.
 */
sealed class Either<out A, out B> {

   abstract val isRight: Boolean
   abstract val isLeft: Boolean

   companion object {

      operator fun <B> invoke(f: () -> B): Try<B> = catch(f)

      fun <A, B> cond(test: Boolean, ifFalse: () -> A, ifTrue: () -> B) = when (test) {
         false -> ifFalse().left()
         true -> ifTrue().right()
      }

      fun <A, B> cond(test: () -> Boolean, ifFalse: () -> A, ifTrue: () -> B) = when (test()) {
         false -> ifFalse().left()
         true -> ifTrue().right()
      }

      fun <A> success(a: A) = Right(a)
      fun failure(t: Throwable) = Left(t)
   }

   data class Left<A>(val a: A) : Either<A, Nothing>() {
      override val isLeft: Boolean = true
      override val isRight: Boolean = false
   }

   data class Right<B>(val b: B) : Either<Nothing, B>() {
      override val isLeft: Boolean = false
      override val isRight: Boolean = true
   }

   inline fun <C> mapLeft(f: (A) -> C): Either<C, B> =
      when (this) {
         is Left -> Left(f(a))
         is Right -> this
      }

   inline fun <D> mapRight(f: (B) -> D): Either<A, D> = map(f)

   inline fun <D> map(f: (B) -> D): Either<A, D> =
      when (this) {
         is Left -> this
         is Right -> Right(f(b))
      }

   inline fun <C> fold(ifLeft: (A) -> C, ifRight: (B) -> C): C = when (this) {
      is Left -> ifLeft(a)
      is Right -> ifRight(b)
   }

   fun <C> fold(ifLeft: C, ifRight: C): C = when (this) {
      is Left -> ifLeft
      is Right -> ifRight
   }

   fun swap(): Either<B, A> = fold({ it.right() }, { it.left() })

   fun getRightOrNull(): B? = fold({ null }, { it })

   fun getLeftOrNull(): A? = fold({ it }, { null })

   inline fun <C, D> bimap(ifLeft: (A) -> C, ifRight: (B) -> D): Either<C, D> =
      fold({ ifLeft(it).left() }, { ifRight(it).right() })

   // process the given function if this is a left, and then return this
   inline fun onLeft(f: (A) -> Unit): Either<A, B> =
      fold({ f(it); this }, { this })

   inline fun onRight(f: (B) -> Unit): Either<A, B> =
      fold({ this }, { f(it); this })

   fun handleLeft(f: (A) -> Unit): Option<B> = fold({
      f(it)
      none
   }, { it.some() })

   fun getRightUnsafe(): B = fold({ throw RuntimeException("Expected right but was $it") }, { it })

   fun getLeftUnsafe(): A = fold({ it }, { throw RuntimeException("Expected left but was $it") })

   fun toValidated(): Validated<A, B> = fold({ it.invalid() }, { it.valid() })

   inline fun <reified C> filterRightIsInstance(otherwise: (B) -> Any): Either<A, C> = when (this) {
      is Left -> this
      is Right -> when (val b = this.b) {
         is C -> b.right()
         else -> (otherwise(this.b) as A).left()
      }
   }

   fun onEach(ifLeft: (A) -> Unit, ifRight: (B) -> Unit): Either<A, B> {
      when (this) {
         is Left -> ifLeft(this.a)
         is Right -> ifRight(this.b)
      }
      return this
   }

   fun toOption(): Option<B> = fold({ none }, { it.some() })
}

inline fun <A, B> Either<A, B>.orElse(other: () -> Either<A, B>): Either<A, B> {
   return fold({ other() }, { it.right() })
}

/**
 * Invokes the given function [f] wrapping the result into an [Either.Right], or, if an exception
 * is thrown, will wrap the throwable into an [Either.Left].
 *
 * @return Either.Right<B> or an Either.Left<Throwable>.
 */
inline fun <B> either(f: () -> B): Either<Throwable, B> = try {
   f().right()
} catch (t: Throwable) {
   t.left()
}

fun <A> Either<Throwable, A>.toTry() = fold({ it.failure() }, { it.success() })

// if this is a left, invokes the given function to return the error into a right, otherwise returns the right
inline fun <A, B> Either<A, B>.recover(ifLeft: (A) -> B): Either<A, B> =
   fold({ ifLeft(it).right() }, { this })

inline fun <A, B> Either<A, B>.recoverWith(ifLeft: (A) -> Either<A, B>): Either<A, B> = when (this) {
   is Either.Left -> ifLeft(this.a)
   is Either.Right -> this
}

fun <A, B> Either<A, Either<A, B>>.flatten(): Either<A, B> = when (this) {
   is Either.Left -> this
   is Either.Right -> b
}

@Deprecated("use com.sksamuel.tabby.`try`.Try")
fun <B> Try<Option<B>>.flatten(ifNone: () -> String): Try<B> = when (this) {
   is Either.Left -> this
   is Either.Right -> when (val b = this.b) {
      is Option.Some -> b.value.right()
      is Option.None -> NoSuchElementException(ifNone()).left()
   }
}

inline fun <A, B, D> Either<A, B>.flatMap(f: (B) -> Either<A, D>): Either<A, D> = when (this) {
   is Either.Left -> this
   is Either.Right -> f(b)
}

@Deprecated("use flatmap")
inline fun <A, B, D> Either<A, B>.flatMapRight(f: (B) -> Either<A, D>): Either<A, D> = when (this) {
   is Either.Left -> this
   is Either.Right -> f(b)
}

inline fun <A, B, C> Either<A, B>.flatMapLeft(f: (A) -> Either<C, B>): Either<C, B> = when (this) {
   is Either.Left -> f(a)
   is Either.Right -> this
}

/**
 * Splits a list of [Either] into a pair of lists, one containing left instances and one containing right instances.
 */
fun <A, B> List<Either<A, B>>.split(): Pair<List<A>, List<B>> {
   val (lefts, rights) = this.partition { it is Either.Left }
   return lefts.map { it.getLeftUnsafe() } to rights.map { it.getRightUnsafe() }
}

inline fun <A, B> Either<A, B>.getLeftOrElse(default: (B) -> A): A =
   fold({ it }, { default(it) })

inline fun <A, B> Either<A, B>.getRightOrElse(default: (A) -> B): B =
   fold({ default(it) }, { it })

inline fun <A, B> Either<A, B>.filter(predicate: (B) -> Boolean, elseIf: (B) -> A): Either<A, B> =
   flatMapRight { if (predicate(it)) it.right() else elseIf(it).left() }

inline fun <A, B> Either<A, B>.leftIfRightIsNull(f: () -> A): Either<A, B> =
   fold({ this }, { if (it == null) Either.Left(f()) else this })

fun <A, B> B?.leftIfNull(ifNull: () -> A): Either<A, B> =
   this?.right() ?: ifNull().left()

fun <A, B> B.leftIf(a: A, eval: (B) -> Boolean): Either<A, B> = if (eval(this)) a.left() else this.right()

fun <A, B> B.rightIf(a: A, eval: (B) -> Boolean): Either<A, B> = if (eval(this)) this.right() else a.left()

fun <A, B> B?.rightIfNotNull(ifNull: () -> A): Either<A, B> =
   this?.right() ?: ifNull().left()

fun <A> A.left() = Either.Left(this)
fun <B> B.right() = Either.Right(this)

@Deprecated("use com.sksamuel.tabby.`try`.catch")
inline fun <B> catching(thunk: () -> B): Either<Throwable, B> = try {
   thunk().right()
} catch (t: Throwable) {
   t.left()
}

@Deprecated("use com.sksamuel.tabby.`try`.catch")
inline fun <A, B> catching(fn: () -> B, handle: (Throwable) -> A): Either<A, B> = try {
   fn().right()
} catch (t: Throwable) {
   handle(t).left()
}

@Deprecated("use com.sksamuel.tabby.`try`.catch")
inline fun <A, B> catching(handle: (Throwable) -> A, thunk: () -> B): Either<A, B> = try {
   thunk().right()
} catch (t: Throwable) {
   handle(t).left()
}

@Deprecated("use com.sksamuel.tabby.`try`.catch")
inline fun <A> catch(thunk: () -> A): Try<A> = try {
   thunk().right()
} catch (t: Throwable) {
   t.left()
}

fun <A, B, C> Either<A, B>.zip(other: Either<A, C>): Either<A, Pair<B, C>> {
   return this.flatMap { b ->
      other.map { c -> Pair(b, c) }
   }
}

fun <E, A, B, C> Either<E, A>.zip(b: Either<E, B>, c: Either<E, C>): Either<E, Triple<A, B, C>> {
   return this.flatMap { a ->
      b.flatMap { b ->
         c.map { c ->
            Triple(a, b, c)
         }
      }
   }
}

fun <A, B, C, D> Either<A, B>.mapN(other: Either<A, C>, f: (B, C) -> D): Either<A, D> {
   return this.flatMap { b ->
      other.map { c -> f(b, c) }
   }
}

fun <A, B, C, D> Either<A, B>.mapNWith(other: Either<A, C>, f: (B, C) -> Either<A, D>): Either<A, D> {
   return this.flatMap { b ->
      other.flatMap { c -> f(b, c) }
   }
}

@Deprecated("use com.sksamuel.tabby.`try`.Try`")
typealias Try<B> = Either<Throwable, B>

@Deprecated("use toEither")
fun <A, B> Option<B>.either(ifEmpty: () -> A): Either<A, B> = fold({ ifEmpty().left() }, { it.right() })

fun <A, B, C> Either<A, List<B>>.mapK(f: (B) -> C): Either<A, List<C>> = fold({ it.left() }, { it.map(f).right() })

@Deprecated("use mapN")
inline fun <A, B, E, R> applicative(
   a: Either<E, A>,
   b: Either<E, B>,
   fn: (A, B) -> R,
): Either<E, R> {
   if (a.isLeft) return a as Either<E, R>
   if (b.isLeft) return b as Either<E, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe()).right()
}

@Deprecated("use mapN")
inline fun <A, B, C, E, R> applicative(
   a: Either<E, A>,
   b: Either<E, B>,
   c: Either<E, C>,
   fn: (A, B, C) -> R,
): Either<E, R> {
   if (a.isLeft) return a as Either<E, R>
   if (b.isLeft) return b as Either<E, R>
   if (c.isLeft) return c as Either<E, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe(), c.getRightUnsafe()).right()
}

@Deprecated("use mapN")
inline fun <A, B, C, D, E, R> applicative(
   a: Either<E, A>,
   b: Either<E, B>,
   c: Either<E, C>,
   d: Either<E, D>,
   fn: (A, B, C, D) -> R,
): Either<E, R> {
   if (a.isLeft) return a as Either<E, R>
   if (b.isLeft) return b as Either<E, R>
   if (c.isLeft) return c as Either<E, R>
   if (d.isLeft) return d as Either<E, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe(), c.getRightUnsafe(), d.getRightUnsafe()).right()
}

@Deprecated("use mapN")
fun <A, B, E, R> app(
   a: Either<E, A>,
   b: Either<E, B>,
   fn: (A, B) -> R,
): Either<E, R> {
   return a.flatMap { _a ->
      b.map { _b ->
         fn(_a, _b)
      }
   }
}

@Deprecated("use mapN")
fun <A, B, C, E, R> app(
   a: Either<E, A>,
   b: Either<E, B>,
   c: Either<E, C>,
   fn: (A, B, C) -> R,
): Either<E, R> {
   return a.flatMap { _a ->
      b.flatMap { _b ->
         c.map { _c ->
            fn(_a, _b, _c)
         }
      }
   }
}

@Deprecated("use mapN")
fun <A, B, C, D, E, R> app(
   a: Either<E, A>,
   b: Either<E, B>,
   c: Either<E, C>,
   d: Either<E, D>,
   fn: (A, B, C, D) -> R,
): Either<E, R> {
   return a.flatMap { _a ->
      b.flatMap { _b ->
         c.flatMap { _c ->
            d.map { _d ->
               fn(_a, _b, _c, _d)
            }
         }
      }
   }
}

@Deprecated("use mapN")
fun <A, B, C, D, E, ERROR, RETURN> app(
   a: Either<ERROR, A>,
   b: Either<ERROR, B>,
   c: Either<ERROR, C>,
   d: Either<ERROR, D>,
   e: Either<ERROR, E>,
   fn: (A, B, C, D, E) -> RETURN,
): Either<ERROR, RETURN> {
   return a.flatMap { _a ->
      b.flatMap { _b ->
         c.flatMap { _c ->
            d.flatMap { _d ->
               e.map { _e ->
                  fn(_a, _b, _c, _d, _e)
               }
            }
         }
      }
   }
}

@Deprecated("use mapN")
inline fun <A, B, C, D, E, L, R> applicative(
   a: Either<L, A>,
   b: Either<L, B>,
   c: Either<L, C>,
   d: Either<L, D>,
   e: Either<L, E>,
   fn: (A, B, C, D, E) -> R,
): Either<L, R> {
   if (a.isLeft) return a as Either<L, R>
   if (b.isLeft) return b as Either<L, R>
   if (c.isLeft) return c as Either<L, R>
   if (d.isLeft) return d as Either<L, R>
   if (e.isLeft) return e as Either<L, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe(), c.getRightUnsafe(), d.getRightUnsafe(), e.getRightUnsafe()).right()
}

@Deprecated("use mapN")
inline fun <A, B, C, D, E, F, L, R> applicative(
   a: Either<L, A>,
   b: Either<L, B>,
   c: Either<L, C>,
   d: Either<L, D>,
   e: Either<L, E>,
   f: Either<L, F>,
   fn: (A, B, C, D, E, F) -> R,
): Either<L, R> {
   if (a.isLeft) return a as Either<L, R>
   if (b.isLeft) return b as Either<L, R>
   if (c.isLeft) return c as Either<L, R>
   if (d.isLeft) return d as Either<L, R>
   if (e.isLeft) return e as Either<L, R>
   if (f.isLeft) return f as Either<L, R>
   return fn(
      a.getRightUnsafe(),
      b.getRightUnsafe(),
      c.getRightUnsafe(),
      d.getRightUnsafe(),
      e.getRightUnsafe(),
      f.getRightUnsafe()
   ).right()
}

inline fun <reified A, reified B> List<Either<A, B>>.sequence(): Either<List<A>, List<B>> {
   val `as` = filterIsInstance<A>()
   val bs = filterIsInstance<B>()
   return if (`as`.isEmpty()) bs.right() else `as`.left()
}

@Deprecated("use com.sksamuel.tabby.`try`.Try`")
fun <A> Try<A>.onFailure(f: (Throwable) -> Unit): Try<A> {
   if (this is Either.Left) f(this.a)
   return this
}
