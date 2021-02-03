package com.sksamuel.tabby.`try`

fun <A, B> Try<A>.zip(other: Try<B>): Try<Pair<A, B>> {
   return this.flatMap { a ->
      other.map { b -> Pair(a, b) }
   }
}

fun <A, B, C> Try<A>.zip(b: Try<B>, c: Try<C>): Try<Triple<A, B, C>> {
   return this.flatMap { a ->
      b.flatMap { b ->
         c.map { c ->
            Triple(a, b, c)
         }
      }
   }
}
