package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try

/**
 * Applies the potentially side effecting function [f] after this IO has completed,
 * regardless of the outcome of this IO.
 *
 * The result of the function is ignored and errors are suppressed.
 *
 * Returns this IO.
 */
fun <A> IO<A>.then(f: suspend () -> Unit): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      return this@then.apply().onFailure { f() }.onSuccess { f() }
   }
}

/**
 * Applies the given effect after this IO has completed, regardless of the outcome.
 * The result of the after effect is ignored.
 *
 * Returns this IO.
 */
fun <A> IO<A>.then(t: IO<Unit>): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      return this@then.apply()
         .onError { t.run() }
         .onSuccess { t.run() }
   }
}

/**
 * Applies the given effect after this IO has completed, regardless of the outcome.
 * The result of the after effect is ignored.
 *
 * Returns this IO.
 */
fun <A> IO<A>.then(ifError: (Throwable) -> Unit, ifSuccess: (A) -> Unit): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      return this@then.run().onError(ifError).onSuccess(ifSuccess)
   }
}
