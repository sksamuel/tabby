package com.sksamuel.tabby.effects

import com.sksamuel.tabby.results.flatten

/**
 * Uses the resource from this resource [R], returning the result and safely
 * closing the resource afterwards. If closing the resource raises an error,
 * then that error is returned over a successful result.
 */
fun <R : AutoCloseable, A> R.use(f: suspend (R) -> Result<A>): Result<A> {
   return runCatching { this.use(f) }.flatten()
}
