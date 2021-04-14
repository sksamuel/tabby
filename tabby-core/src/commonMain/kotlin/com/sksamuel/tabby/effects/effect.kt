package com.sksamuel.tabby.effects

interface Effect {
   suspend fun <A> IO<A>.await(): A = run().fold({ throw it }, { it })
}

object EffectImpl : Effect

/**
 * Invokes the given function [f] wrapping the result into an effect.
 *
 * The function allows for effects to be executed using [await], handling failures.
 */
fun <A> effect(f: suspend Effect.() -> A): IO<A> = IO {
   EffectImpl.f()
}
