package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.success

/**
 * Recovers from an error by applying the given function to create an effect, which is then executed.
 */
fun <A> IO<A>.recover(f: (Throwable) -> IO<A>): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> = this@recover.apply().fold({ f(it).apply() }, { it.success() })
}

/**
 * Recovers from an error by invoking the given function [f]. The return value of this
 * function will replace the error in the effect.
 */
fun <A> IO<A>.recoverEffect(f: suspend (Throwable) -> A): IO<A> = recover { IO.effect { f(it) } }
