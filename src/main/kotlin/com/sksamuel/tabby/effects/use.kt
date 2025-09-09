package com.sksamuel.tabby.effects

import com.sksamuel.tabby.results.flatMap

/**
 * Uses the resource [R] contained in this [Result], returning the result of the
 * function [f], and safely closing the resource afterwards.
 *
 * If closing the resource raises an error, then that error is returned in place
 * of a successful result.
 */
inline fun <R : AutoCloseable, A> Result<R>.use(f: (R) -> Result<A>): Result<A> {
   return flatMap { it.use(f) }
}
