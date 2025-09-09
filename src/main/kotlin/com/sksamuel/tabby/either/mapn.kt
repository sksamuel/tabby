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
   e: Either<ERROR, E>,
   fn: (A, B, C, D, E) -> R,
): Either<ERROR, R> {
   if (a.isLeft) return a as Either<ERROR, R>
   if (b.isLeft) return b as Either<ERROR, R>
   if (c.isLeft) return c as Either<ERROR, R>
   if (d.isLeft) return d as Either<ERROR, R>
   if (e.isLeft) return e as Either<ERROR, R>
   return fn(a.getRightUnsafe(), b.getRightUnsafe(), c.getRightUnsafe(), d.getRightUnsafe(), e.getRightUnsafe()).right()
}

inline fun <A, B, C, D, E, F, ERROR, R> Either.Companion.mapN(
   a: Either<ERROR, A>,
   b: Either<ERROR, B>,
   c: Either<ERROR, C>,
   d: Either<ERROR, D>,
   e: Either<ERROR, E>,
   f: Either<ERROR, F>,
   fn: (A, B, C, D, E, F) -> R,
): Either<ERROR, R> {
   if (a.isLeft) return a as Either<ERROR, R>
   if (b.isLeft) return b as Either<ERROR, R>
   if (c.isLeft) return c as Either<ERROR, R>
   if (d.isLeft) return d as Either<ERROR, R>
   if (e.isLeft) return e as Either<ERROR, R>
   if (f.isLeft) return f as Either<ERROR, R>
   return fn(
      a.getRightUnsafe(),
      b.getRightUnsafe(),
      c.getRightUnsafe(),
      d.getRightUnsafe(),
      e.getRightUnsafe(),
      f.getRightUnsafe(),
   ).right()
}

inline fun <A, B, C, D, E, F, G, ERROR, R> Either.Companion.mapN(
   a: Either<ERROR, A>,
   b: Either<ERROR, B>,
   c: Either<ERROR, C>,
   d: Either<ERROR, D>,
   e: Either<ERROR, E>,
   f: Either<ERROR, F>,
   g: Either<ERROR, G>,
   fn: (A, B, C, D, E, F, G) -> R,
): Either<ERROR, R> {
   if (a.isLeft) return a as Either<ERROR, R>
   if (b.isLeft) return b as Either<ERROR, R>
   if (c.isLeft) return c as Either<ERROR, R>
   if (d.isLeft) return d as Either<ERROR, R>
   if (e.isLeft) return e as Either<ERROR, R>
   if (f.isLeft) return f as Either<ERROR, R>
   if (g.isLeft) return g as Either<ERROR, R>
   return fn(
      a.getRightUnsafe(),
      b.getRightUnsafe(),
      c.getRightUnsafe(),
      d.getRightUnsafe(),
      e.getRightUnsafe(),
      f.getRightUnsafe(),
      g.getRightUnsafe(),
   ).right()
}
