package com.sksamuel.tabby.results

interface Catching {
   fun <A> Result<A>.bind() = this.getOrThrow()
}

inline fun <R> catching(f: Catching.() -> R): Result<R> {
   return try {
      Result.success(object : Catching {}.f())
   } catch (e: Throwable) {
      Result.failure(e)
   }
}
