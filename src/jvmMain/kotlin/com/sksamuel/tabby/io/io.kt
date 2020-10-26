package com.sksamuel.tabby.io

/**
 * Returns an effect that acquires an [AutoCloseable] resource using the given, possibly, effectful [acquire] function.
 * The resource is guaranteed to be closed before the effect completes.
 * Any error in acquisition is ignored.
 */
fun <A : AutoCloseable, B> IO.Companion.use(acquire: suspend () -> A,
                                            use: (A) -> Task<B>): Task<B> {
   return effect(acquire).flatMap { a ->
      val close = effect { a.close() }
      use(a).tap { close }.tapError { close }
   }
}
