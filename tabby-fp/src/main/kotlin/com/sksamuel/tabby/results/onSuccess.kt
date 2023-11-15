package com.sksamuel.tabby.results

inline fun <A> Result<A?>.onSuccessIfNotNull(f: (A) -> Unit) =
   onSuccess { if (it != null) f(it) }

inline fun <A> Result<A?>.onSuccessIfNull(f: () -> Unit): Result<A?> =
   this.fold({
      if (it == null) f()
      it.success()
   }, { it.failure() })

inline fun <A> Result<A>.onSuccessIf(predicate: (A) -> Boolean, f: (A) -> Unit): Result<A> =
   this.fold({
      if (predicate(it)) f(it)
      it.success()
   }, { it.failure() })

inline fun Result<Boolean>.onSuccessIfTrue(f: () -> Unit): Result<Boolean> =
   this.fold({
      if (it) f()
      it.success()
   }, { it.failure() })

inline fun Result<Boolean>.onSuccessIfFalse(f: () -> Unit): Result<Boolean> =
   this.fold({
      if (!it) f()
      it.success()
   }, { it.failure() })

inline fun <A> Result<A?>.onSuccessIfNotNull(predicate: (A) -> Boolean, f: (A) -> Unit): Result<A?> =
   this.fold({
      if ((it != null) && predicate(it)) f(it)
      it.success()
   }, { it.failure() })
