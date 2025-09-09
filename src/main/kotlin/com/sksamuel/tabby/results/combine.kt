package com.sksamuel.tabby.results

import com.sksamuel.tabby.Tuple4
import com.sksamuel.tabby.Tuple5

inline fun <A, B> Result<A>.combine(fn: (A) -> Result<B>): Result<Pair<A, B>> {
   return this.flatMap { a ->
      fn(a).map { b -> Pair(a, b) }
   }
}

inline fun <A, B, C> Result<Pair<A, B>>.combine(f: (A, B) -> Result<C>): Result<Triple<A, B, C>> {
   return this.flatMap { (a, b) ->
      f(a, b).map { c -> Triple(a, b, c) }
   }
}

inline fun <A, B, C, D> Result<Triple<A, B, C>>.combine(f: (A, B, C) -> Result<D>): Result<Tuple4<A, B, C, D>> {
   return this.flatMap { (a, b, c) ->
      f(a, b, c).map { d -> Tuple4(a, b, c, d) }
   }
}

inline fun <A, B, C, D, E> Result<Tuple4<A, B, C, D>>.combine(f: (A, B, C, D) -> Result<E>): Result<Tuple5<A, B, C, D, E>> {
   return this.flatMap { (a, b, c, d) ->
      f(a, b, c, d).map { e -> Tuple5(a, b, c, d, e) }
   }
}
