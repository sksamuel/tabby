package com.sksamuel.tabby.either

inline fun <A, B, ERROR, R> Either.Companion.mapN(
   a: Either<ERROR, A>,
   b: Either<ERROR, B>,
   fn: (A, B) -> R,
): Either<ERROR, R> {
   if (a.isLeft) return a as Either<ERROR, R>
   if (b.isLeft) return b as Either<ERROR, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe()).right()
}

inline fun <A, B, C, ERROR, R> Either.Companion.mapN(
   a: Either<ERROR, A>,
   b: Either<ERROR, B>,
   c: Either<ERROR, C>,
   fn: (A, B, C) -> R,
): Either<ERROR, R> {
   if (a.isLeft) return a as Either<ERROR, R>
   if (b.isLeft) return b as Either<ERROR, R>
   if (c.isLeft) return c as Either<ERROR, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe(), c.getRightUnsafe()).right()
}

inline fun <A, B, C, D, ERROR, R> Either.Companion.mapN(
   a: Either<ERROR, A>,
   b: Either<ERROR, B>,
   c: Either<ERROR, C>,
   d: Either<ERROR, D>,
   fn: (A, B, C, D) -> R,
): Either<ERROR, R> {
   if (a.isLeft) return a as Either<ERROR, R>
   if (b.isLeft) return b as Either<ERROR, R>
   if (c.isLeft) return c as Either<ERROR, R>
   if (d.isLeft) return d as Either<ERROR, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe(), c.getRightUnsafe(), d.getRightUnsafe()).right()
}


inline fun <A, B, C, D, E, ERROR, R> Either.Companion.mapN(
   a: Either<ERROR, A>,
   b: Either<ERROR, B>,
   c: Either<ERROR, C>,
   d: Either<ERROR, D>,
   e: Either<ERROR, D>,
   fn: (A, B, C, D) -> R,
): Either<ERROR, R> {
   if (a.isLeft) return a as Either<ERROR, R>
   if (b.isLeft) return b as Either<ERROR, R>
   if (c.isLeft) return c as Either<ERROR, R>
   if (d.isLeft) return d as Either<ERROR, R>
   if (e.isLeft) return d as Either<ERROR, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe(), c.getRightUnsafe(), d.getRightUnsafe()).right()
}
