package com.sksamuel.tabby.results

inline fun <A> Result<A?>.onFailureOrNull(f: () -> Unit): Result<A?> {
   return this.fold(
      {
         if (it == null) f()
         it.success()
      },
      {
         f()
         it.failure()
      }
   )
}
