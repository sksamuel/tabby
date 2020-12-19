package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.right

/**
 * Evaluate and run each effect in the structure, in sequence,
 * and collect discarding failed ones.
 */
fun <T> IO.Companion.collectSuccess(vararg effects: IO<T>): IO<List<T>> = collectSuccess(effects.asList())

fun <T> IO.Companion.collectSuccess(effects: List<IO<T>>): IO<List<T>> = object : IO<List<T>>() {
   override suspend fun apply(): Try<List<T>> {
      return effects.fold(emptyList<T>()) { acc, op ->
         op.apply().fold({ acc }, { acc + it })
      }.right()
   }
}
