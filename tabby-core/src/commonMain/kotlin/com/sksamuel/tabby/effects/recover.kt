package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.right

/**
 * Recovers from an error by invoking the given function [f]. The return value of this
 * function will replace the error in the effect.
 */
fun <A> IO<A>.recover(f: (Throwable) -> A): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> = this@recover.apply().fold({ f(it).right() }, { it.right() })
}

/**
 * Recovers from an error by applying the given function to retrieve an effect, which is then executed.
 */
fun <A> IO<A>.recoverWith(f: (Throwable) -> IO<A>): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> = this@recoverWith.apply().fold({ f(it).apply() }, { it.right() })
}
