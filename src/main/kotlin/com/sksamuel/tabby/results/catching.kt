package com.sksamuel.tabby.results

import kotlinx.coroutines.TimeoutCancellationException

interface Catching {
   fun <A> Result<A>.bind() = this.getOrThrow()
}

inline fun <R> catching(f: Catching.() -> R): Result<R> = runCatching {
   object : Catching {}.f()
}.onFailure { throwable ->
   when (throwable) {
      is VirtualMachineError, // OutOfMemory and the like
      is ThreadDeath, // deprecated but still possibly thrown by old libraries etc
      is LinkageError, // dependency incompatibilities basically
      is kotlin.coroutines.cancellation.CancellationException, // should never be caught as doing so breaks coroutines
      is kotlinx.coroutines.CancellationException, // should never be caught as doing so breaks coroutines
      is java.util.concurrent.CancellationException, // should never be caught as doing so breaks coroutines
      is TimeoutCancellationException, // currently a subclass of the above but that will change
      is InterruptedException // same as above but threads
      -> {
         throw throwable // never catch the above, always rethrow them
      }
   }
}
