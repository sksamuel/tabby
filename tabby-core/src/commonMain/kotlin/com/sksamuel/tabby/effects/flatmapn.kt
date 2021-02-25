package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try

fun <A, B, R> IO.Companion.flatMapN(first: IO<A>, second: IO<B>, f: (A, B) -> IO<R>): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a -> second.apply().flatMap { b -> f(a, b).apply() } }
   }
}

fun <A, B, C, R> IO.Companion.flatMapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   f: (A, B, C) -> IO<R>,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               f(a, b, c).apply()
            }
         }
      }
   }
}

fun <A, B, C, D, R> IO.Companion.flatMapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   fourth: IO<D>,
   f: (A, B, C, D) -> IO<R>,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               fourth.apply().flatMap { d ->
                  f(a, b, c, d).apply()
               }
            }
         }
      }
   }
}

fun <A, B, C, D, E, R> IO.Companion.flatMapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   fourth: IO<D>,
   fifth: IO<E>,
   f: (A, B, C, D, E) -> IO<R>,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               fourth.apply().flatMap { d ->
                  fifth.apply().flatMap { e ->
                     f(a, b, c, d, e).apply()
                  }
               }
            }
         }
      }
   }
}

fun <A, B, C, D, E, F, R> IO.Companion.flatMapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   fourth: IO<D>,
   fifth: IO<E>,
   sixth: IO<F>,
   f: (A, B, C, D, E, F) -> IO<R>,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               fourth.apply().flatMap { d ->
                  fifth.apply().flatMap { e ->
                     sixth.apply().flatMap { f ->
                        f(a, b, c, d, e, f).apply()
                     }
                  }
               }
            }
         }
      }
   }
}

fun <A, B, C, D, E, F, G, R> IO.Companion.flatMapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   fourth: IO<D>,
   fifth: IO<E>,
   sixth: IO<F>,
   seventh: IO<G>,
   f: (A, B, C, D, E, F, G) -> IO<R>,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               fourth.apply().flatMap { d ->
                  fifth.apply().flatMap { e ->
                     sixth.apply().flatMap { f ->
                        seventh.apply().flatMap { g ->
                           f(a, b, c, d, e, f, g).apply()
                        }
                     }
                  }
               }
            }
         }
      }
   }
}

fun <A, B, C, D, E, F, G, H, R> IO.Companion.flatMapN(
   first: IO<A>,
   second: IO<B>,
   third: IO<C>,
   fourth: IO<D>,
   fifth: IO<E>,
   sixth: IO<F>,
   seventh: IO<G>,
   eighth: IO<H>,
   f: (A, B, C, D, E, F, G, H) -> IO<R>,
): IO<R> = object : IO<R>() {
   override suspend fun apply(): Try<R> {
      return first.apply().flatMap { a ->
         second.apply().flatMap { b ->
            third.apply().flatMap { c ->
               fourth.apply().flatMap { d ->
                  fifth.apply().flatMap { e ->
                     sixth.apply().flatMap { f ->
                        seventh.apply().flatMap { g ->
                           eighth.apply().flatMap { h ->
                              f(a, b, c, d, e, f, g, h).apply()
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}

