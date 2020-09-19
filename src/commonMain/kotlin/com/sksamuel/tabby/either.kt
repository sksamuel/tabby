package com.sksamuel.tabby

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

      fun <A, B> cond(test: Boolean, ifFalse: () -> A, ifTrue: () -> B) = when (test) {
         false -> ifFalse().left()
         true -> ifTrue().right()
      }

      fun <A, B> cond(test: () -> Boolean, ifFalse: () -> A, ifTrue: () -> B) = when (test()) {
         false -> ifFalse().left()
         true -> ifTrue().right()
      }
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
      none()
   }, { it.some() })

   fun getRightUnsafe(): B = fold({ throw RuntimeException("Expected right but was $it") }, { it })

   fun getLeftUnsafe(): A = fold({ it }, { throw RuntimeException("Expected left but was $it") })

   fun toValidated(): Validated<A, B> = fold({ it.invalid() }, { it.valid() })

   inline fun <reified C> filterRightIsInstance(otherwise: (B) -> Any): Either<A, C> = when (this) {
      is Left -> this
      is Right -> when (val b = this.b) {
         is C -> b.right() as Either<A, C>
         else -> (otherwise(this.b) as A).left()
      }
   }
}

/**
 * Invokes the given function [f] wrapping the result into an [Either.Right], or, if an exception
 * is thrown, will wrap the throwable into an [Either.Left].
 *
 * @return Either.Right<B> or an Either.Left<Throwable>.
 */
inline fun <B> either(f: () -> B): Either<Throwable, B> = Try(f).toEither { it }

// if this is a left, invokes the given function to return the error into a right, otherwise returns the right
inline fun <A, B> Either<A, B>.recoverLeft(ifLeft: (A) -> B): Either<A, B> =
   fold({ ifLeft(it).right() }, { this })

fun <A, B> Either<A, Either<A, B>>.flatten(): Either<A, B> = when (this) {
   is Either.Left -> this
   is Either.Right -> b
}

inline fun <A, B, D> Either<A, B>.flatMap(f: (B) -> Either<A, D>): Either<A, D> = when (this) {
   is Either.Left -> this
   is Either.Right -> f(b)
}

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

inline fun <A> Either<A, *>.getLeftOrElse(default: () -> A): A =
   fold({ it }, { default() })

inline fun <B> Either<*, B>.getRightOrElse(default: () -> B): B =
   fold({ default() }, { it })

inline fun <A, B> Either<A, B>.getRightOrHandle(default: (A) -> B): B =
   fold({ default(it) }, { it })

inline fun <A, B> Either<A, B>.filterRight(predicate: (B) -> Boolean, elseIf: (B) -> A): Either<A, B> =
   flatMapRight { if (predicate(it)) it.right() else elseIf(it).left() }

inline fun <A, B> Either<A, B>.leftIfRightIsNull(f: () -> A): Either<A, B> =
   fold({ this }, { if (it == null) Either.Left(f()) else this })

fun <A, B> B?.leftIfNull(ifNull: () -> A): Either<A, B> =
   this?.right() ?: ifNull().left()

fun <A, B> B?.rightIfNotNull(ifNull: () -> A): Either<A, B> =
   this?.right() ?: ifNull().left()

fun <A> A.left() = Either.Left(this)
fun <B> B.right() = Either.Right(this)

@Deprecated("use either {} ")
inline fun <B> catching(thunk: () -> B): Either<Throwable, B> = try {
   thunk().right()
} catch (t: Throwable) {
   t.left()
}

@Deprecated("use either {} ")
inline fun <A, B> catching(fn: () -> B, handle: (Throwable) -> A): Either<A, B> = try {
   fn().right()
} catch (t: Throwable) {
   handle(t).left()
}

@Deprecated("use either {} ")
inline fun <A, B> catching(handle: (Throwable) -> A, thunk: () -> B): Either<A, B> = try {
   thunk().right()
} catch (t: Throwable) {
   handle(t).left()
}

fun <A, B> Option<B>.either(ifEmpty: () -> A): Either<A, B> = fold({ ifEmpty().left() }, { it.right() })

fun <A, B, C> Either<A, List<B>>.mapK(f: (B) -> C): Either<A, List<C>> = fold({ it.left() }, { it.map(f).right() })

inline fun <A, B, E, R> applicative(
   a: Either<E, A>,
   b: Either<E, B>,
   fn: (A, B) -> R
): Either<E, R> {
   if (a.isLeft) return a as Either<E, R>
   if (b.isLeft) return b as Either<E, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe()).right()
}

inline fun <A, B, C, E, R> applicative(
   a: Either<E, A>,
   b: Either<E, B>,
   c: Either<E, C>,
   fn: (A, B, C) -> R
): Either<E, R> {
   if (a.isLeft) return a as Either<E, R>
   if (b.isLeft) return b as Either<E, R>
   if (c.isLeft) return c as Either<E, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe(), c.getRightUnsafe()).right()
}

inline fun <A, B, C, D, E, R> applicative(
   a: Either<E, A>,
   b: Either<E, B>,
   c: Either<E, C>,
   d: Either<E, D>,
   fn: (A, B, C, D) -> R
): Either<E, R> {
   if (a.isLeft) return a as Either<E, R>
   if (b.isLeft) return b as Either<E, R>
   if (c.isLeft) return c as Either<E, R>
   if (d.isLeft) return d as Either<E, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe(), c.getRightUnsafe(), d.getRightUnsafe()).right()
}

inline fun <A, B, C, D, E, L, R> applicative(
   a: Either<L, A>,
   b: Either<L, B>,
   c: Either<L, C>,
   d: Either<L, D>,
   e: Either<L, E>,
   fn: (A, B, C, D, E) -> R
): Either<L, R> {
   if (a.isLeft) return a as Either<L, R>
   if (b.isLeft) return b as Either<L, R>
   if (c.isLeft) return c as Either<L, R>
   if (d.isLeft) return d as Either<L, R>
   if (e.isLeft) return e as Either<L, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe(), c.getRightUnsafe(), d.getRightUnsafe(), e.getRightUnsafe()).right()
}

inline fun <A, B, C, D, E, F, L, R> applicative(
   a: Either<L, A>,
   b: Either<L, B>,
   c: Either<L, C>,
   d: Either<L, D>,
   e: Either<L, E>,
   f: Either<L, F>,
   fn: (A, B, C, D, E, F) -> R
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
