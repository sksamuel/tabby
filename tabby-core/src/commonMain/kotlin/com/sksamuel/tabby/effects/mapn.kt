package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try

fun <A, B, R> IO.Companion.mapN(first: IO<A>, second: IO<B>, f: (A, B) -> R): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
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

fun <A, B, C, D, R> IO.Companion.mapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   fourth: IO<D>,
   f: (A, B, C, D) -> R,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               fourth.apply().map { d ->
                  f(a, b, c, d)
               }
            }
         }
      }
   }
}

fun <A, B, C, D, E, R> IO.Companion.mapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   fourth: IO<D>,
   fifth: IO<E>,
   f: (A, B, C, D, E) -> R,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               fourth.apply().flatMap { d ->
                  fifth.apply().map { e ->
                     f(a, b, c, d, e)
                  }
               }
            }
         }
      }
   }
}
