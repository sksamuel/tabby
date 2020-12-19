package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.right

/**
 * Evaluate and run each effect in the structure, in sequence,
 * and collect discarding failed ones.
 */
fun <A> IO.Companion.collectSuccess(vararg effects: IO<A>): IO<List<A>> = collectSuccess(effects.asList())

fun <A> IO.Companion.collectSuccess(effects: List<IO<A>>): IO<List<A>> = object : IO<List<A>>() {
   override suspend fun apply(): Try<List<A>> {
      return effects.fold(emptyList<A>()) { acc, op ->
         op.apply().fold({ acc }, { acc + it })
      }.right()
   }
}

fun <A> List<IO<A>>.collectSuccess(): IO<List<A>> = IO.collectSuccess(this)
