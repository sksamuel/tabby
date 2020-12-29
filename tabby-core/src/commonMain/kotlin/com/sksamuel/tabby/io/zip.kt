package com.sksamuel.tabby.io

import com.sksamuel.tabby.Tuple4
import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.flatMap

fun <E, A, B> IO.Companion.zip(
   a: IO<E, A>,
   b: IO<E, B>,
): IO<E, Pair<A, B>> = object : IO<E, Pair<A, B>>() {
   override suspend fun apply(): Either<E, Pair<A, B>> {
      return a.apply().flatMap { a ->
         b.apply().map { b ->
            Pair(a, b)
         }
      }
   }
}

fun <E, A, B, C> IO.Companion.zip(
   a: IO<E, A>,
   b: IO<E, B>,
   c: IO<E, C>,
): IO<E, Triple<A, B, C>> = object : IO<E, Triple<A, B, C>>() {
   override suspend fun apply(): Either<E, Triple<A, B, C>> {
      return a.apply().flatMap { a ->
         b.apply().flatMap { b ->
            c.apply().map { c ->
               Triple(a, b, c)
            }
         }
      }
   }
}

fun <E, A, B, C, D> IO.Companion.zip(
   a: IO<E, A>,
   b: IO<E, B>,
   c: IO<E, C>,
   d: IO<E, D>,
): IO<E, Tuple4<A, B, C, D>> = object : IO<E, Tuple4<A, B, C, D>>() {
   override suspend fun apply(): Either<E, Tuple4<A, B, C, D>> {
      return a.apply().flatMap { a ->
         b.apply().flatMap { b ->
            c.apply().flatMap { c ->
               d.apply().map { d ->
                  Tuple4(a, b, c, d)
               }
            }
         }
      }
   }
}
