package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.flatMap

fun <A, B, R> IO.Companion.flatMapN(first: IO<A>, second: IO<B>, f: (A, B) -> IO<R>): IO<R> = object : IO<R>() {
   override suspend fun apply(): Either<Throwable, R> {
      return first.apply().flatMap { a -> second.apply().flatMap { b -> f(a, b).apply() } }
   }
}

fun <A, B, C, R> IO.Companion.flatMapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   f: (A, B, C) -> IO<R>,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               f(a, b, c).apply()
            }
         }
      }
   }
}

fun <A, B, C, D, R> IO.Companion.flatMapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   fourth: IO<D>,
   f: (A, B, C, D) -> IO<R>,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               fourth.apply().flatMap { d ->
                  f(a, b, c, d).apply()
               }
            }
         }
      }
   }
}

fun <A, B, C, D, E, R> IO.Companion.flatMapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   fourth: IO<D>,
   fifth: IO<E>,
   f: (A, B, C, D, E) -> IO<R>,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               fourth.apply().flatMap { d ->
                  fifth.apply().flatMap { e ->
                     f(a, b, c, d, e).apply()
                  }
               }
            }
         }
      }
   }
}
