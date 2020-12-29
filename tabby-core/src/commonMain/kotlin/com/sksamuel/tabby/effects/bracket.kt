package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try

/**
 * Acquires a resource of type R using the given [acquire] effect, passes that resource to a [use] effect,
 * with the [release] effect guaranteed to execute after the completion of the use effect.
 */
fun <R, A> IO.Companion.bracket(acquire: IO<R>, use: (R) -> IO<A>, release: (R) -> IO<Unit>): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      return acquire.flatMap { r -> use(r).then(release(r)) }.apply()
   }
}
