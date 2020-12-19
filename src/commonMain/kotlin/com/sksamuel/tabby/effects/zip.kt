@file:Suppress("unused")

package com.sksamuel.tabby.effects

import com.sksamuel.tabby.Tuple4
import com.sksamuel.tabby.Tuple5
import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.flatMap
import kotlinx.coroutines.delay

fun <A, B> IO.Companion.zip(
   a: IO<A>,
   b: IO<B>,
): IO<Pair<A, B>> = object : IO<Pair<A, B>>() {
   override suspend fun apply(): Try<Pair<A, B>> {
      return a.apply().flatMap { a ->
         b.apply().map { b ->
            Pair(a, b)
         }
      }
   }
}


fun <A, B, C> IO.Companion.zip(
   a: IO<A>,
   b: IO<B>,
   c: IO<C>,
): IO<Triple<A, B, C>> = object : IO<Triple<A, B, C>>() {
   override suspend fun apply(): Try<Triple<A, B, C>> {
      delay(1000)
      return a.apply().flatMap { a ->
         b.apply().flatMap { b ->
            c.apply().map { c ->
               Triple(a, b, c)
            }
         }
      }
   }
}

fun <A, B, C, D> IO.Companion.zip(
   a: IO<A>,
   b: IO<B>,
   c: IO<C>,
   d: IO<D>,
): IO<Tuple4<A, B, C, D>> = object : IO<Tuple4<A, B, C, D>>() {
   override suspend fun apply(): Try<Tuple4<A, B, C, D>> {
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

fun <A, B, C, D, E> IO.Companion.zip(
   a: IO<A>,
   b: IO<B>,
   c: IO<C>,
   d: IO<D>,
   e: IO<E>,
): IO<Tuple5<A, B, C, D, E>> = object : IO<Tuple5<A, B, C, D, E>>() {
   override suspend fun apply(): Try<Tuple5<A, B, C, D, E>> {
      return a.apply().flatMap { a ->
         b.apply().flatMap { b ->
            c.apply().flatMap { c ->
               d.apply().flatMap { d ->
                  e.apply().map { e ->
                     Tuple5(a, b, c, d, e)
                  }
               }
            }
         }
      }
   }
}

