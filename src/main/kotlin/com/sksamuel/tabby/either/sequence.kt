package com.sksamuel.tabby.either

/**
 * Gather's together Either's effects.
 *
 * Specifically, returns the first [Either.Left], or a List of all [Either.Right]s.
 */
inline fun <reified A, reified B> List<Either<A, B>>.sequence(): Either<List<A>, List<B>> {
   val `as` = filterIsInstance<A>()
   val bs = filterIsInstance<B>()
   return if (`as`.isEmpty()) bs.right() else `as`.left()
}
