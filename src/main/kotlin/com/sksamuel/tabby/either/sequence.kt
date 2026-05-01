package com.sksamuel.tabby.either

/**
 * Gathers together Either's effects.
 *
 * If any element is an [Either.Left], returns a [Either.Left] containing all the left values.
 * Otherwise returns an [Either.Right] containing all the right values.
 */
fun <A, B> List<Either<A, B>>.sequence(): Either<List<A>, List<B>> {
   val lefts = filterIsInstance<Either.Left<A>>().map { it.a }
   return if (lefts.isEmpty()) {
      filterIsInstance<Either.Right<B>>().map { it.b }.right()
   } else {
      lefts.left()
   }
}
