package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try

/**
 * Surrounds this IO with a safe before and after operation.
 */
@Deprecated("Do not use")
fun <A, R> IO<A>.brace(before: () -> R, after: (R) -> Unit): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      val r = before()
      return this@brace.apply().onFailure { after(r) }.onSuccess { after(r) }
   }
}
