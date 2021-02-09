package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.orElse

/**
 * Invokes and returns the result of [other] if the result of this IO is a failure.
 */
@Deprecated("use recover")
fun <A> IO<A>.orElse(other: IO<A>): IO<A> = this.orElse { other }

/**
 * Invokes and returns the result of [other] if the result of this IO is a failure.
 */
@Deprecated("use recover")
fun <A> IO<A>.orElse(other: () -> IO<A>): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> = this@orElse.apply().orElse { other().apply() }
}

/**
 * Invokes and returns the result of the [other] effectul function if the result of this IO is a failure.
 */
@Deprecated("use recoverEffect")
fun <A> IO<A>.orElseEffect(other: suspend () -> A): IO<A> = this.orElse(IO.effect(other))
