package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.right

/**
 * Returns an effect that contains the results of the given effects. If any of the given
 * effects fails, this effect will fail and any successes will be dropped.
 */
fun <A> IO.Companion.traverse(vararg effects: IO<A>): IO<List<A>> = traverse(effects.asList())

fun <A> IO.Companion.traverse(effects: List<IO<A>>): IO<List<A>> = object : IO<List<A>>() {
   override suspend fun apply(): Try<List<A>> {
      return effects.fold(emptyList<A>()) { acc, effect ->
         when (val result = effect.apply()) {
            is Either.Left -> return result
            is Either.Right -> acc + result.b
         }
      }.right()
   }
}

/**
 * Returns an effect that contains the results of the given effects. If any of the given
 * effects fails, this effect will fail and any successes will be dropped.
 */
fun <A> List<IO<A>>.traverse(): IO<List<A>> = IO.traverse(this)
