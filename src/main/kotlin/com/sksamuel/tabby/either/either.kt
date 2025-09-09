@file:Suppress("unused")

package com.sksamuel.tabby.either

/**
 * A right-biased implementation of Either.
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

   /**
    * If this [Either] is a left, then runs the side effecting function [f],
    * and returns the receiver.
    */
   inline fun onLeft(f: (A) -> Unit): Either<A, B> =
      fold({ f(it); this }, { this })

   /**
    * If this [Either] is a right, then runs the side effecting function [f],
    * and returns the receiver.
    */
   inline fun onRight(f: (B) -> Unit): Either<A, B> =
      fold({ this }, { f(it); this })

   inline fun onEach(ifLeft: (A) -> Unit, ifRight: (B) -> Unit): Either<A, B> {
      when (this) {
         is Left -> ifLeft(this.a)
         is Right -> ifRight(this.b)
      }
      return this
   }

   fun getRightUnsafe(): B = fold({ throw RuntimeException("Expected right but was $it") }, { it })

   fun getLeftUnsafe(): A = fold({ it }, { throw RuntimeException("Expected left but was $it") })

   @Deprecated("To be replaced")
   inline fun <reified C> filterRightIsInstance(otherwise: (B) -> Any): Either<A, C> = when (this) {
      is Left -> this
      is Right -> when (val b = this.b) {
         is C -> b.right()
         else -> (otherwise(this.b) as A).left()
      }
   }
}

fun <A> A.left() = Either.Left(this)

fun <B> B.right() = Either.Right(this)

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

inline fun <A, B> Either<A, B>.leftIfRightIsNull(f: () -> A): Either<A, B> =
   fold({ this }, { if (it == null) Either.Left(f()) else this })

fun <A, B> B?.leftIfNull(ifNull: () -> A): Either<A, B> =
   this?.right() ?: ifNull().left()

fun <A, B> B.leftIf(a: A, eval: (B) -> Boolean): Either<A, B> = if (eval(this)) a.left() else this.right()

fun <A, B> B.rightIf(a: A, eval: (B) -> Boolean): Either<A, B> = if (eval(this)) this.right() else a.left()

fun <A, B> B?.rightIfNotNull(ifNull: () -> A): Either<A, B> =
   this?.right() ?: ifNull().left()
