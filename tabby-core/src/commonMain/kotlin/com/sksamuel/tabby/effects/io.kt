@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.MonadControlException
import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.catch
import com.sksamuel.tabby.`try`.failure
import com.sksamuel.tabby.`try`.getValueOrElse
import com.sksamuel.tabby.`try`.success
import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.none
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
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

   class Pure<A>(private val a: A) : IO<A>() {
      override suspend fun apply(): Try<A> = Try.success(a)
   }

   class Failed(private val t: suspend () -> Throwable) : IO<Nothing>() {
      override suspend fun apply(): Try<Nothing> = Try.failure(t())
   }

   class Effect<A>(private val f: suspend () -> Try<A>) : IO<A>() {
      override suspend fun apply(): Try<A> = f()
   }

   companion object {

      /**
       * Returns a successful effect that wraps [Unit].
       */
      val unit = pure(Unit)

      /**
       * Returns a successful effort that wraps null.
       */
      @Deprecated("use IO.Null")
      val nullz = pure(null)

      val Null = pure(null)

      /**
       * Returns a successful effect that wraps [none].
       */
      val empty = pure(none)

      /**
       * Wraps a potentially throwing, effectful function as an IO.
       */
      operator fun <A> invoke(f: suspend () -> A): IO<A> = Effect { catch { f() } }

      /**
       * Alias to [invoke].
       */
      fun <A> effect(f: suspend () -> A): IO<A> = invoke(f)

      /**
       * Wraps a strict value as a successfully completed IO.
       */
      fun <A> pure(a: A): IO<A> = Pure(a)

      /**
       * Wraps a safe function as a successfully completed IO.
       */
      fun <A> success(f: () -> A): IO<A> = object : IO<A>() {
         override suspend fun apply(): Try<A> = Try.success(f())
      }

      /**
       * Wraps a strict value as a failed IO.
       */
      fun failure(t: Throwable): IO<Nothing> = object : IO<Nothing>() {
         override suspend fun apply(): Try<Nothing> = Try.failure(t)
      }

      /**
       * Wraps a function as a failed IO.
       */
      fun failure(f: suspend () -> Throwable): IO<Nothing> = Failed(f)

      /**
       * Returns a failed IO with the given message converted to a runtime exception.
       */
      fun failure(msg: String): IO<Nothing> = Failed { RuntimeException(msg) }

      fun <A> from(result: Try<A>): IO<A> = object : IO<A>() {
         override suspend fun apply(): Try<A> = result
      }

      fun <A> from(f: suspend () -> Try<A>): IO<A> = object : IO<A>() {
         override suspend fun apply(): Try<A> = f()
      }

      /**
       * Wraps an infallible, effectful function as a lazy IO.
       */
      fun <A> safe(f: suspend () -> A): IO<A> = object : IO<A>() {
         override suspend fun apply(): Try<A> = Try.success(f())
      }

      /**
       * Evaluate the predicate, wrapping the effectful function [success] if the predicate is true,
       * wrapping the effectful function [error] otherwise.
       */
      fun <A> cond(predicate: Boolean, success: suspend () -> A, error: suspend () -> Throwable): IO<A> =
         if (predicate) effect(success) else failure(error)

      /**
       * Evaluate the [predicate] function, wrapping the effectful function [success] if the predicate is true,
       * wrapping the effectful function [error] otherwise.
       */
      fun <A> cond(predicate: () -> Boolean, success: suspend () -> A, error: suspend () -> Throwable): IO<A> =
         if (predicate()) effect(success) else failure(error)

      /**
       * Reduces IOs using the supplied function, working sequentially, or returns the
       * first failure.
       */
      fun <A> reduce(first: IO<A>, vararg rest: IO<A>, f: (A, A) -> A): IO<A> = object : IO<A>() {
         override suspend fun apply(): Try<A> {
            if (rest.isEmpty()) return first.apply()
            var acc = first.apply().fold({ return it.failure() }, { it })
            rest.forEach { op ->
               when (val result = op.apply()) {
                  is Try.Failure -> return result
                  is Try.Success -> acc = f(acc, result.value)
               }
            }
            return acc.success()
         }
      }
   }

   /**
    * Runs the safe, side effecting function [f] upon the success of this effect.
    *
    * Returns this effect.
    */
   fun forEachEffect(f: suspend (A) -> Unit): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> = this@IO.apply().onSuccess { f(it) }
   }

   /**
    * Runs the effect generated by the given function [f] upon the success of this effect.
    *
    * Returns this effect.
    */
   fun forEach(f: (A) -> IO<Unit>): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> = this@IO.apply().onSuccess { f(it) }
   }

   /**
    * Returns an effect which maps the value of this effect upon success.
    */
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
   @Deprecated("this is now called flatMapEffect")
   fun <B> mapEffect(f: suspend (A) -> B): IO<B> = flatMap { t -> effect { f(t) } }

   fun mapError(f: (Throwable) -> Throwable): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> = this@IO.apply().mapError(f)
   }

   fun <B> flatMap(f: (A) -> IO<B>): IO<B> = object : IO<B>() {
      override suspend fun apply(): Try<B> = this@IO.apply().flatMap { f(it).apply() }
   }

   fun <B> flatMapEffect(f: suspend (A) -> B): IO<B> {
      val ff: (A) -> IO<B> = { IO { f(it) } }
      return this.flatMap(ff)
   }

   /**
    * Returns an effect that executes the given function if this effect evaluates to an error.
    */
   fun onError(f: (Throwable) -> Unit): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> {
         val result = this@IO.apply()
         result.onFailure(f)
         return result
      }
   }

   /**
    * If this effect evaluates to an error, runs the [also] effect.
    * The result of [also] is disregarded, and the result of this effect
    * is returned.
    */
   fun <B> onError(also: IO<B>): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> {
         val result = this@IO.apply()
         also.run()
         return result
      }
   }

   /**
    * Alias for [forEach].
    */
   fun onSuccess(f: suspend (A) -> Unit): IO<A> = forEachEffect(f)

   /**
    * If this effect evaluates to success, runs the [also] effect.
    * The result of [also] is disregarded, and the result of this effect
    * is returned.
    */
   fun <B> onSuccess(also: IO<B>): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> {
         val result = this@IO.apply()
         also.run()
         return result
      }
   }

   /**
    * Ignores any success value from this effect, returning an effect that produces Unit.
    */
   fun unit(): IO<Unit> = object : IO<Unit>() {
      override suspend fun apply(): Try<Unit> = this@IO.apply().map { }
   }

   fun <B> zip(other: IO<B>): IO<Pair<A, B>> = IO.zip(this, other)

   /**
    * Provides a context switch for this IO.
    */
   fun onContext(context: CoroutineContext): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> = withContext(context) { this@IO.apply() }
   }

   /**
    * Returns a new IO which is just this IO but with the success result
    * replaced with the given strict value.
    */
   fun <B> with(b: B): IO<B> = object : IO<B>() {
      override suspend fun apply(): Try<B> = this@IO.apply().map { b }
   }

   /**
    * Wraps this IO in a synchronization operation that will ensure the effect
    * only takes place once a permit is acquired from the given semaphore.
    *
    * While waiting to acquire, the effect will suspend.
    */
   fun synchronize(semaphore: Semaphore): IO<A> = object : IO<A>() {
      override suspend fun apply(): Try<A> = semaphore.withPermit { this@IO.apply() }
   }

   suspend fun runOrElse(ifError: (Throwable) -> Unit): A? {
      return run().onFailure(ifError).getValueOrNull()
   }

   /**
    * Executes this IO, with the calling coroutine as the context.
    * Returns the successful result or null.
    */
   suspend fun runOrNull(): A? = apply().getValueOrNull()

   /**
    * Executes this IO, with the calling coroutine as the context.
    * Returns the successful result wrapped in an option, or none if the execution failed.
    */
   suspend fun runOrNone(): Option<A> = apply().toOption()

   /**
    * Executes this IO, returning the result as a [Try].
    */
   suspend fun run(): Try<A> = this@IO.apply()

   /**
    * Evaluates this effect, returning the value if successful, or throwing if evaluation fails.
    */
   suspend fun runUnsafe(): A = run().getValueUnsafe()

   suspend fun <B> funFold(ifError: (Throwable) -> B, ifSuccess: (A) -> B): B {
      return run().fold(ifError, ifSuccess)
   }

   suspend operator fun not() = run().getValueOrElse { throw MonadControlException(it) }
}

fun <A> IO<IO<A>>.flatten(): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
      return this@flatten.apply().flatMap { it.apply() }
   }
}

fun <A> IO<A?>.flatMapIfNull(f: () -> IO<A>): IO<A> = this.flatMap { it?.pure() ?: f() }
fun <A> IO<A?>.mapIfNull(f: () -> A): IO<A> = this.map { it ?: f() }

fun <A, B> IO<A?>.mapIfNotNull(f: (A) -> B): IO<B?> = this.map { if (it == null) null else f(it) }
fun <A, B> IO<A?>.flatMapIfNotNull(f: (A) -> IO<B>): IO<B?> = this.flatMap { if (it == null) IO.pure(null) else f(it) }

fun <A : Any> IO<List<A?>>.filterNotNull(): IO<List<A>> = this.map { it.filterNotNull() }

fun <A> A.pure() = IO.pure(this)
fun Throwable.fail(): IO<Nothing> = IO.failure(this)

/**
 * Returns an IO that gets it's value from this Try.
 */
fun <A> Try<A>.effect(): IO<A> = IO.from(this)

// Unproductive IO, will never succeed
typealias UIO = IO<Nothing>      // Cannot succeed
