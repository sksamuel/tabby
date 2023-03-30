package com.sksamuel.tabby.results

inline fun <A> Result<A?>.onSuccessIfNotNull(f: (A) -> Unit) =
   onSuccess { if (it != null) f(it) }

inline fun <A> Result<A?>.onSuccessIfNull(f: () -> Unit): Result<A?> {
   return this.fold({
      if (it == null) f()
      it.success()
   }, { it.failure() })
}
