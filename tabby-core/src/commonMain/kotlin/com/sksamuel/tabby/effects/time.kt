package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try
import kotlin.jvm.JvmName
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * Upon completion of this IO, invokes the function [f] to retrieve an effect which is executed, with the
 * duration of this IO as the argument.
 *
 * The result of the effect is ignored and errors are suppressed.
 *
 * Returns this IO.
 */
@OptIn(ExperimentalTime::class)
@OverloadResolutionByLambdaReturnType
fun <A> IO<A>.time(f: (Duration) -> IO<Unit>): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      val (result, time) = measureTimedValue { this@time.apply() }
      f(time).run() // ignore result
      return result
   }
}

/**
 * Applies the safe function [f] after this IO has completed, with the
 * duration of this IO as the argument.
 *
 * Returns this IO.
 */
@OptIn(ExperimentalTime::class)
@JvmName("timeUnit")
@OverloadResolutionByLambdaReturnType
fun <A> IO<A>.time(f: (Duration) -> Unit): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      val (result, time) = measureTimedValue { this@time.apply() }
      effect { f(time) }.run() // ignore result
      return result
   }
}
