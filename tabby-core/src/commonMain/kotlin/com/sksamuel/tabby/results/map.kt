package com.sksamuel.tabby.results

inline fun <A> Result<A?>.mapNull(fn: () -> A): Result<A> =
   map { it ?: fn() }

inline fun <A, B> Result<A?>.mapIfNotNull(fn: (A) -> B): Result<B?> =
   map { if (it == null) null else fn(it) }

inline fun <A, B> Result<A?>.flatMapIfNotNull(fn: (A) -> Result<B>): Result<B?> =
   flatMap { if (it == null) null.success() else fn(it) }

inline fun <A, B> Result<A>.flatMap(fn: (A) -> Result<B>): Result<B> =
   fold({ fn(it) }, { Result.failure(it) })

inline fun <A> Result<A?>.flatMapNull(fn: () -> Result<A>): Result<A> =
   flatMap { it?.success() ?: fn() }

