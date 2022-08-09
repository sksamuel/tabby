package com.sksamuel.tabby.results

import com.sksamuel.tabby.Tuple4
import com.sksamuel.tabby.Tuple5

fun <A, B> Result<A>.zip(other: Result<B>): Result<Pair<A, B>> {
   return this.flatMap { a ->
      other.map { b -> Pair(a, b) }
   }
}

fun <A, B, C> Result<A>.zip(b: Result<B>, c: Result<C>): Result<Triple<A, B, C>> {
   return this.flatMap { a ->
      b.flatMap { b ->
         c.map { c ->
            Triple(a, b, c)
         }
      }
   }
}

fun <A, B, C> Result.Companion.zip(a: Result<A>, b: Result<B>, c: Result<C>): Result<Triple<A, B, C>> = a.zip(b, c)

fun <A, B, C, D> Result.Companion.zip(
   a: Result<A>,
   b: Result<B>,
   c: Result<C>,
   d: Result<D>,
): Result<Tuple4<A, B, C, D>> = a.flatMap { a ->
   b.flatMap { b ->
      c.flatMap { c ->
         d.map { d ->
            Tuple4(a, b, c, d)
         }
      }
   }
}

fun <A, B, C, D, E> Result.Companion.zip(
   a: Result<A>,
   b: Result<B>,
   c: Result<C>,
   d: Result<D>,
   e: Result<E>,
): Result<Tuple5<A, B, C, D, E>> = a.flatMap { a ->
   b.flatMap { b ->
      c.flatMap { c ->
         d.flatMap { d ->
            e.map { e ->
               Tuple5(a, b, c, d, e)
            }
         }
      }
   }
}
