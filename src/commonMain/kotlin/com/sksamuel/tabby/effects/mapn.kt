package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.flatMap

fun <A, B, R> mapN(first: IO<A>, second: IO<B>, f: (A, B) -> R): IO<R> = object : IO<R>() {
   override suspend fun apply(): Either<Throwable, R> {
      return first.apply().flatMap { a -> second.apply().map { b -> f(a, b) } }
   }
}

fun <E, A, B, R> mapN(
   first: com.sksamuel.tabby.io.IO<E, A>,
   second: com.sksamuel.tabby.io.IO<E, B>,
   f: (A, B) -> R
): com.sksamuel.tabby.io.IO<E, R> = object : com.sksamuel.tabby.io.IO<E, R>() {
   override suspend fun apply(): Either<E, R> {
      return first.apply().flatMap { a -> second.apply().map { b -> f(a, b) } }
   }
}

fun <A, B, C, R> IO.Companion.mapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   f: (A, B, C) -> R,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().map { c ->
               f(a, b, c)
            }
         }
      }
   }
}
