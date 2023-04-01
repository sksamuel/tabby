package com.sksamuel.tabby.either

inline fun <A, B, D> Either<A, B>.flatMap(f: (B) -> Either<A, D>): Either<A, D> = when (this) {
   is Either.Left -> this
   is Either.Right -> f(b)
}

inline fun <A, B, C> Either<A, B>.flatMapLeft(f: (A) -> Either<C, B>): Either<C, B> = when (this) {
   is Either.Left -> f(a)
   is Either.Right -> this
}
