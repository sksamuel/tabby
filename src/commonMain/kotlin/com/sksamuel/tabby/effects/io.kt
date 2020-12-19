package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.catch
import com.sksamuel.tabby.either.flatMap
import com.sksamuel.tabby.either.left
import com.sksamuel.tabby.either.right
import com.sksamuel.tabby.io.Task
import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.none
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * A value of type IO<A> describes an effect that may fail with a Throwable, run forever, or produce a single A.
 *
 * IO values are immutable, and all IO functions produce new IO values.
 *
 * IO can be executed as a regular suspendable function in the current coroutine scope.
 */
abstract class IO<out A> {

   abstract suspend fun apply(): Try<A>

   class Pure<A>(private val a: () -> A) : IO<A>() {
      override suspend fun apply(): Try<A> = Try.success(a())
   }

   class Failed(private val t: suspend () -> Throwable) : IO<Nothing>() {
      override suspend fun apply(): Try<Nothing> = Try.failure(t())
   }

   class Effect<A>(private val f: suspend () -> A) : IO<A>() {
      override suspend fun apply(): Try<A> = catch { f() }
   }

   class EffectTotal<A>(private val f: suspend () -> A) : IO<A>() {
      override suspend fun apply(): Try<A> = Try.success(f())
   }

   /**
    * Returns an effect that runs the safe, side effecting function on the success of this effect.
    */
   fun forEach(f: suspend (A) -> Unit): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> = this@IO.apply().onRight { f(it) }
   }

   /**
    * Returns an effect that runs the given task on the success of this effect.
    */
   fun forEach(f: (A) -> Task<Unit>): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> = this@IO.apply().onRight { f(it) }
   }

   fun <B> map(f: (A) -> B): IO<B> = object : IO<B>() {
      override suspend fun apply(): Try<B> = this@IO.apply().map(f)
   }

   /**
    * Returns an effect which applies the given side effecting function to it's success,
    * wrapping the supplied function in an effect before execution.
    *
    * In other words, this is a shorthand for the following.
    *
    * task.flatMap { IO.effect { f(it) } }
    */
   fun <B> mapEffect(f: suspend (A) -> B): IO<B> = flatMap { t -> effect { f(t) } }

   fun mapError(f: (Throwable) -> Throwable): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> = this@IO.apply().mapLeft { f(it) }
   }

   fun <B> flatMap(f: (A) -> IO<B>): IO<B> = object : IO<B>() {
      override suspend fun apply(): Try<B> = this@IO.apply().flatMap { f(it).apply() }
   }

   /**
    * Executes the given function if this is an error.
    * Returns this IO.
    */
   fun onError(f: (Throwable) -> Unit): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> {
         val result = this@IO.apply()
         if (result is Either.Left) f(result.a)
         return result
      }
   }

   /**
    * Ignores any success value from this effect, returning an effect that produces Unit.
    */
   fun unit(): IO<Unit> = object : IO<Unit>() {
      override suspend fun apply(): Try<Unit> = this@IO.apply().map { }
   }

   /**
    * Provides a context switch for this IO.
    */
   fun onContext(context: CoroutineContext): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> = withContext(context) { this@IO.apply() }
   }

   /**
    * Returns a new IO which is just this IO but with the result of a successful execution
    * replaced with the given strict value.
    */
   fun <B> with(b: B): IO<B> = object : IO<B>() {
      override suspend fun apply(): Try<B> = this@IO.apply().map { b }
   }

   /**
    * Executes this IO, with the calling coroutine as the context.
    * Returns the successful result or null.
    */
   suspend fun runOrNull(): A? = apply().getRightOrNull()

   /**
    * Executes this IO, with the calling coroutine as the context.
    * Returns the successful result wrapped in an option, or none if the execution failed.
    */
   suspend fun runOrNone(): Option<A> = apply().toOption()

   /**
    * Executes this IO, returning the result as a [Try].
    */
   suspend fun run(): Try<A> = this@IO.apply()

   suspend fun runUnsafe(): A = run().getRightUnsafe()

   suspend fun runOrThrow() = run().fold({ throw it }, { it })

   /**
    * Alias for forEach
    */
   fun onSuccess(f: suspend (A) -> Unit): IO<A> = forEach(f)

   companion object {

      /**
       * Returns a successful effect that wraps Unit.
       */
      val unit = pure(Unit)

      /**
       * Returns a successful effect that wraps [none].
       */
      val empty = pure(none)

      /**
       * Wraps a strict value as a successfully completed IO.
       */
      fun <A> pure(a: A): IO<A> = Pure { a }

      /**
       * Wraps a safe function as a successfully completed IO.
       */
      fun <A> success(f: () -> A): IO<A> = Pure(f)

      /**
       * Wraps a strict value as a failed IO.
       */
      fun failure(t: Throwable): IO<Nothing> = Failed { t }

      /**
       * Wraps a function as a failed IO.
       */
      fun failure(f: suspend () -> Throwable): IO<Nothing> = Failed(f)

      /**
       * Returns a failed IO with the given message converted to a runtime exception.
       */
      fun failure(msg: String): IO<Nothing> = Failed { RuntimeException(msg) }

      /**
       * Wraps a potentially throwing, effectful function as a lazy IO.
       */
      fun <A> effect(f: suspend () -> A): IO<A> = Effect(f)

      fun <A> from(result: Try<A>): IO<A> = object : IO<A>() {
         override suspend fun apply(): Try<A> = result
      }

      fun <A> from(f: suspend () -> Try<A>): IO<A> = object : IO<A>() {
         override suspend fun apply(): Try<A> = f()
      }

      /**
       * Wraps an infallible, effectful function as a lazy IO.
       */
      fun <T> safe(f: suspend () -> T): IO<T> = EffectTotal(f)

      /**
       * Evaluate the predicate, wrapping the effectful function [success] if the predicate is true,
       * wrapping the effectful function [error] otherwise.
       */
      fun <E, A> cond(predicate: Boolean, success: suspend () -> A, error: suspend () -> Throwable): IO<A> =
         if (predicate) effect(success) else failure(error)

      /**
       * Evaluate the [predicate] function, wrapping the effectful function [success] if the predicate is true,
       * wrapping the effectful function [error] otherwise.
       */
      fun <E, A> cond(predicate: () -> Boolean, success: suspend () -> A, error: suspend () -> Throwable): IO<A> =
         if (predicate()) effect(success) else failure(error)

      /**
       * Reduces IOs using the supplied function, working sequentially, or returns the
       * first failure.
       */
      fun <E, A> reduce(first: IO<A>, vararg rest: IO<A>, f: (A, A) -> A): IO<A> = object : IO<A>() {
         override suspend fun apply(): Try<A> {
            if (rest.isEmpty()) return first.apply()
            var acc = first.apply().fold({ return it.left() }, { it })
            rest.forEach { op ->
               when (val result = op.apply()) {
                  is Either.Left -> return result
                  is Either.Right -> acc = f(acc, result.b)
               }
            }
            return acc.right()
         }
      }
   }
}


fun <A> IO<IO<A>>.flatten(): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      return this@flatten.apply().flatMap { it.apply() }
   }
}

fun Throwable.fail(): IO<Nothing> = IO.failure(this)
fun <A> Try<A>.effect(): IO<A> = IO.from(this)

// Unproductive IO, will never succeed
typealias UIO = IO<Nothing>      // Cannot succeed
