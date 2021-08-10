package com.sksamuel.tabby.results

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
