package com.sksamuel.tabby.either

import com.sksamuel.tabby.option.Option

fun <A, B> Either<A, Option<B>>.flatten(f: () -> A): Either<A, B> = when (this) {
   is Either.Left -> this
   is Either.Right -> b.fold({ f().left() }, { it.right() })
}

fun <A, B> Either<A, Option<B>>.trifold(fa: (A) -> Unit, fn: (B) -> Unit, fs: (B) -> Unit) {
   this.fold(fa, { it.fold(fn, fs) })
}
