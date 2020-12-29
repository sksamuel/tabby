package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.right

/**
 * Evaluate and run each effect in the structure, in sequence,
 * and collect discarding failed ones.
 */
fun <A> IO.Companion.collect(vararg effects: IO<A>): IO<List<A>> = collect(effects.asList())

fun <A> IO.Companion.collect(effects: List<IO<A>>): IO<List<A>> = object : IO<List<A>>() {
   override suspend fun apply(): Try<List<A>> {
      return effects.fold(emptyList<A>()) { acc, op ->
         op.apply().fold({ acc }, { acc + it })
      }.right()
   }
}

/**
 * Evaluate each effect in the list in order, discarding failed ones and returning successes.
 */
fun <A> List<IO<A>>.collect(): IO<List<A>> = IO.collect(this)

/**
 * Evaluate each effect in the list in order, discarding failed ones and returning successes
 * that match the given predicate
 */
fun <A> List<IO<A>>.collect(predicate: (A) -> Boolean): IO<List<A>> = object : IO<List<A>>() {
   override suspend fun apply(): Try<List<A>> = this@collect.collect().apply().map { it.filter(predicate) }
}
