package com.sksamuel.tabby.either

import com.sksamuel.tabby.option.Option
import kotlin.experimental.ExperimentalTypeInference

fun <A, B> Either<A, Option<B>>.flatten(f: () -> A): Either<A, B> = when (this) {
   is Either.Left -> this
   is Either.Right -> b.fold({ f().left() }, { it.right() })
}

fun <A, B> Either<A, Option<B>>.trifold(fa: (A) -> Unit, fn: () -> Unit, fs: (B) -> Unit) {
   this.fold(fa, { it.fold(fn, fs) })
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@Deprecated("Use map and then flatten, which allows passing in an error message")
fun <B, C> Either<Throwable, B>.flatMap(f: (B) -> Option<C>): Either<Throwable, C> {
   return this@flatMap.flatMap { b ->
      f(b).fold({ NoSuchElementException().left() }, { it.right() })
   }
}
