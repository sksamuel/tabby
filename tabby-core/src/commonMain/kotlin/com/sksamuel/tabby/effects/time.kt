package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try
import kotlin.jvm.JvmName
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * Upon completion of this IO, invokes the function [f] to retrieve an IO which is executed.
 * The argument to [f] is the duration of this IO.
 *
 * The result of the effect is ignored and errors are suppressed.
 *
 * Returns this IO.
 */
@OptIn(ExperimentalTime::class)
@OverloadResolutionByLambdaReturnType
fun <A> IO<A>.time(f: (Duration) -> IO<Unit>): IO<A> = object : IO<A> {
   override suspend fun apply(): Try<A> {
      val (result, time) = measureTimedValue { this@time.apply() }
      f(time).run()
      return result
   }
}

/**
 * Applies the potentially side effecting function [f] after this IO has completed, with the
 * duration of this IO as the argument.
 *
 * Any errors in the effect are ignored.
 *
 * Returns this IO.
 */
@OptIn(ExperimentalTime::class)
@JvmName("timeUnit")
@OverloadResolutionByLambdaReturnType
fun <A> IO<A>.time(f: (Duration) -> Unit): IO<A> = this.time { time -> IO.effect { f(time) } }
