package com.sksamuel.tabby.results

interface Catching {
   fun <A> Result<A>.bind() = this.getOrThrow()
}

inline fun <R> catching(f: Catching.() -> R): Result<R> = runCatching {
   object : Catching {}.f()
}
