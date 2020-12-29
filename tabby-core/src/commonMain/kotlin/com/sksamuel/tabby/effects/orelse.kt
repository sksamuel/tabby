package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.orElse

/**
 * Invokes and returns the result of [other] if the result of this IO is a failure.
 */
fun <A> IO<A>.orElse(other: IO<A>): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> = this.apply().orElse { other.apply() }
}
