package com.sksamuel.tabby.effects

import kotlin.time.Duration
import kotlin.time.measureTimedValue

/**
 * Executes the given function [f], invoking [onResult] with the duration upon completion.
 *
 * Any errors in [onResult] are discarded.
 */
suspend fun <A> time(onResult: suspend (Duration) -> Unit, f: suspend () -> Result<A>): Result<A> {
   val (result, time) = measureTimedValue { f() }
   runCatching { onResult(time) }
   return result
}
