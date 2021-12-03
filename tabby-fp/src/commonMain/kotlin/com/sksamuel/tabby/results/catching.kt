package com.sksamuel.tabby.results

object Catch {
   fun <A> Result<A>.get() = this.getOrThrow()
}

inline fun <R> catching(f: Catch.() -> R): Result<R> = runCatching { Catch.f() }
