package com.sksamuel.tabby.either

fun <A, B, C> Either<A, B>.zip(other: Either<A, C>): Either<A, Pair<B, C>> {
   return this.flatMap { b ->
      other.map { c -> Pair(b, c) }
   }
}

fun <E, A, B, C> Either<E, A>.zip(b: Either<E, B>, c: Either<E, C>): Either<E, Triple<A, B, C>> {
   return this.flatMap { a ->
      b.flatMap { b ->
         c.map { c ->
            Triple(a, b, c)
         }
      }
   }
}
