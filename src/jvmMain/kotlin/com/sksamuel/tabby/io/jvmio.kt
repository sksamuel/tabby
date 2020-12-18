@file:JvmName("iojvm.kt")

package com.sksamuel.tabby.io

/**
 * Returns an effect that acquires an [AutoCloseable] resource using the given
 * [acquire] effect, and then applies that resource to the given [apply] effect.
 *
 * The resource is guaranteed to be closed before the effect completes.
 * Any error in closing the resource is ignored.
 */
@Deprecated("use use")
fun <A : AutoCloseable, B> IO.Companion.useWith(
   acquire: Task<A>,
   apply: (A) -> Task<B>,
): Task<B> {
   return acquire.flatMap { a ->
      val close = effect { a.close() }
      apply(a).tap { close }.tapError { close }
   }
}

fun <A : AutoCloseable, B> IO.Companion.use(
   acquire: Task<A>,
   apply: (A) -> Task<B>,
): Task<B> {
   return acquire.flatMap { a ->
      val close = effect { a.close() }
      apply(a).tap { close }.tapError { close }
   }
}

fun <A : AutoCloseable, B> IO.Companion.use(
   acquire: Task<A>,
   apply: suspend (A) -> B,
): Task<B> {
   return acquire.flatMap { a ->
      val close = effect { a.close() }
      effect { apply(a) }.then(close)
   }
}

/**
 * Returns an effect that acquires an [AutoCloseable] resource using the given
 * [acquire] function, and then applies that resource to the given [apply] function.
 *
 * Both acquire and use functions can be effectful and will be handed correctly if an error is thrown.
 *
 * The resource is guaranteed to be closed before the effect completes.
 * Any error in closing the resource is ignored.
 */
fun <A : AutoCloseable, B> IO.Companion.use(
   acquire: suspend () -> A,
   apply: suspend (A) -> B,
): Task<B> {
   return effect(acquire).flatMap { a ->
      val close = effect { a.close() }
      effect { apply(a) }.tap { close }.tapError { close }
   }
}

/**
 * For a Task that contains an autocloseable, will execute the given side effecting
 * function, closing this resource safely after the function has completed (either
 * successfully, or with an error).
 */
fun <A : AutoCloseable, B> Task<A>.use(f: suspend (A) -> B): Task<B> {
   return this.flatMap { a ->
      val close = IO.effect { a.close() }
      IO.effect { f(a) }.tap { close }.tapError { close }
   }
}

/**
 * For a Task that contains an autocloseable, will execute the given effect,
 * closing this resource safely after the effect has completed (either
 * successfully, or with an error).
 */
fun <A : AutoCloseable, B> Task<A>.useWith(f: (A) -> Task<B>): Task<B> {
   return this.flatMap { a ->
      val close = IO.effect { a.close() }
      f(a).tap { close }.tapError { close }
   }
}
