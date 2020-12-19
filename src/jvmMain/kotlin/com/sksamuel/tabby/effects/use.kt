package com.sksamuel.tabby.effects

/**
 * For a Task that contains an autocloseable, will execute the given side effecting
 * function, closing this resource safely after the function has completed (either
 * successfully, or with an error).
 */
fun <A : AutoCloseable, B> IO<A>.useEffect(f: suspend (A) -> B): IO<B> {
   return this.flatMap { a ->
      val close = IO.effect { a.close() }
      IO.effect { f(a) }.then(close)
   }
}

/**
 * For a Task that contains an autocloseable, will execute the given effect,
 * closing this resource safely after the effect has completed (either
 * successfully, or with an error).
 */
fun <A : AutoCloseable, B> IO<A>.use(f: (A) -> IO<B>): IO<B> {
   return this.flatMap { a ->
      val close = IO.effect { a.close() }
      f(a).then(close)
   }
}
