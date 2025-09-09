package com.sksamuel.tabby.either

import com.sksamuel.tabby.results.failure
import com.sksamuel.tabby.results.success

/**
 * Given an [Either] where the left case is a [Throwable], returns a [Result] which
 * contains a failure if the either is a left, or a success if the either is a right.
 */
fun <B> Either<Throwable, B>.toResult(): Result<B> =
   fold({ it.failure() }, { it.success() })

/**
 * Given an [Either] returns a [Result] which contains a failure if the either
 * is a left, or a success if the either is a right. In the case of a left, the given
 * function [f] is used to map the left type to a [Throwable]
 */
fun <A, B> Either<A, B>.toResult(f: (A) -> Throwable): Result<B> =
   fold({ f(it).failure() }, { it.success() })
