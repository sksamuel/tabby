package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.right

/**
 * Returns an effect that contains the results of the given effects. If any of the given
 * effects fails, this effect will fail and any successes will be dropped.
 */
fun <T> IO.Companion.traverse(vararg effects: IO<T>): IO<List<T>> = traverse(effects.asList())

fun <T> IO.Companion.traverse(effects: List<IO<T>>): IO<List<T>> = object : IO<List<T>>() {
   override suspend fun apply(): Try<List<T>> {
      return effects.fold(emptyList<T>()) { acc, effect ->
         when (val result = effect.apply()) {
            is Either.Left -> return result
            is Either.Right -> acc + result.b
         }
      }.right()
   }
}
