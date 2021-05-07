package com.sksamuel.tabby.`try`

import kotlin.js.JsName

interface Catch {
   @JsName("getValue")
   fun <A> Try<A>.value(): A = fold({ throw it }, { it })
   fun <A> Try<A>.raise(): Unit {
      if (this is Try.Failure) throw this.error
   }
}

object CatchImpl : Catch

/**
 * Invokes the given function [f] wrapping the result into a [Try.Success], or, if an exception
 * is thrown, will wrap the throwable into an [Try.Failure].
 *
 * Exposes [get] to extract values from Try monads, handling the Failure case by early
 * returning that failure.
 */
inline fun <A> catch(f: Catch.() -> A): Try<A> = try {
   CatchImpl.f().success()
} catch (t: Throwable) {
   t.failure()
}
