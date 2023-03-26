package com.sksamuel.tabby.effects

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Executes the given function [f], invoking [onResult] with the duration upon completion.
 *
 * Any errors in [onResult] are discarded.
 */
suspend fun <A> time(onResult: suspend (Duration) -> Unit, f: suspend () -> Result<A>): Result<A> {
   val start = System.currentTimeMillis()
   val result = f()
   runCatching { onResult((System.currentTimeMillis() - start).milliseconds) }
   return result
}
