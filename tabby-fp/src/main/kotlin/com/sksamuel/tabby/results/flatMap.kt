package com.sksamuel.tabby.results

inline fun <A, B> Result<A>.flatMap(fn: (A) -> Result<B>): Result<B> =
   fold({ fn(it) }, { Result.failure(it) })

inline fun <A, B> Result<A>.flatMapIf(p: (A) -> Boolean, f: (A) -> Result<B>, default: B): Result<B> =
   flatMap { if (p(it)) f(it) else default.success() }

inline fun <A, B> Result<A>.flatMapIf(p: (A) -> Boolean, f: (A) -> Result<B>, g: (A) -> Result<B>): Result<B> =
   flatMap { if (p(it)) f(it) else g(it) }

inline fun <A, B> Result<A?>.flatMapIfNotNull(fn: (A) -> Result<B>): Result<B?> =
   flatMap { if (it == null) null.success() else fn(it) }

inline fun <A> Result<A?>.flatMapIfNull(fn: () -> Result<A>): Result<A> =
   flatMap { it?.success() ?: fn() }
