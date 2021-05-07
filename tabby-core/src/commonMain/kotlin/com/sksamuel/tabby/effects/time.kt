package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.catch
import com.sksamuel.tabby.`try`.failure
import kotlin.jvm.JvmName
import kotlin.time.Duration
import kotlin.time.measureTimedValue

/**
 * Upon completion of this IO, invokes the function [f] to retrieve an effect which is executed, with the
 * duration of this IO as the argument.
 *
 * The result of the effect returned by [f] is ignored and errors are suppressed.
 *
 * Returns this IO.
 */
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
@JvmName("timeUnit")
@OverloadResolutionByLambdaReturnType
fun <A> IO<A>.time(f: (Duration) -> Unit): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      val (result, time) = measureTimedValue { this@time.apply() }
      effect { f(time) }.run() // ignore result
      return result
   }
}

/**
 * Executes the given function [f], invoking [onResult] with the duration upon completion.
 * Returns the value of [f], unless [onResult] fails, then it will return that failure.
 */
suspend fun <A> time(onResult: suspend (Duration) -> Unit, f: suspend () -> Try<A>): Try<A> {
   val (result, time) = measureTimedValue { f() }
   return catch { onResult(time) }.fold({ it.failure() }, { result })
}
