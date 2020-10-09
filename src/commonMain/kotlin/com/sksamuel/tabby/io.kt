package com.sksamuel.tabby

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext

/**
 * A value of type IO[E, T] describes an effect that may fail with an E, run forever, or produce a single T.
 *
 * IO values are immutable, and all IO functions produce new IO values.
 *
 * IO can be executed as a regular suspendable function in the current coroutine scope.
 */
sealed class IO<out E, out T> {

   abstract suspend fun apply(): Either<E, T>

   class Failure<E>(private val error: E) : IO<E, Nothing>() {
      override suspend fun apply() = error.left()
   }

   class Pure<T>(private val t: T) : IO<Nothing, T>() {
      override suspend fun apply() = t.right()
   }

   class Success<T>(private val f: () -> T) : IO<Nothing, T>() {
      override suspend fun apply(): Either<Nothing, T> = f().right()
   }

   class Effect<T>(private val f: suspend () -> T) : IO<Throwable, T>() {
      override suspend fun apply() = either { f() }
   }

   class EffectE<E, T>(private val f: suspend () -> Either<E, T>) : IO<E, T>() {
      override suspend fun apply(): Either<E, T> = f()
   }

   class EffectTotal<T>(private val f: suspend () -> T) : UIO<T>() {
      override suspend fun apply() = f().right()
   }

   class WithTimeout<E, T>(private val duration: Long,
                           private val ifError: (TimeoutCancellationException) -> E,
                           private val underlying: IO<E, T>) : IO<E, T>() {
      override suspend fun apply(): Either<E, T> {
         return try {
            withTimeout(duration) { underlying.apply() }
         } catch (e: TimeoutCancellationException) {
            ifError(e).left()
         }
      }
   }

   class MapFn<E, T, U>(val f: (T) -> U, private val underlying: IO<E, T>) : IO<E, U>() {
      override suspend fun apply(): Either<E, U> = underlying.apply().map { f(it) }
   }

   class MapErrorFn<E, T, E2>(private val f: (E) -> E2, private val underlying: IO<E, T>) : IO<E2, T>() {
      override suspend fun apply() = underlying.apply().mapLeft(f)
   }

   class WithContext<E, T>(private val io: IO<E, T>, private val context: CoroutineContext) : IO<E, T>() {
      override suspend fun apply(): Either<E, T> = withContext(context) { io.apply() }
   }

   class Bracket<T, U>(private val acquire: () -> T,
                       private val use: (T) -> U,
                       private val release: (T) -> Unit) : Task<U>() {
      override suspend fun apply(): Either<Throwable, U> {
         return try {
            val t = acquire()
            try {
               use(t).right()
            } finally {
               release(t)
            }
         } catch (t: Throwable) {
            t.left()
         }
      }
   }

   companion object {

      /**
       * Wraps a strict value as a successfully completed IO.
       */
      fun <T> pure(t: T): UIO<T> = Pure(t)

      /**
       * Wraps a strict value as a failed IO.
       */
      fun <E> fail(e: E): Failed<E> = Failure(e)

      /**
       * Wraps a function as a successfully completed IO.
       */
      fun <T> success(f: () -> T): UIO<T> = Success(f)

      /**
       * Wraps a potentially throwing effectful function as a lazy IO.
       */
      fun <T> effect(f: suspend () -> T): Task<T> = Effect(f)

      /**
       * Wraps a effectful either function as a lazy IO.
       */
      fun <E, T> effectE(f: suspend () -> Either<E, T>): IO<E, T> = EffectE(f)

      /**
       * Wraps an infallible effectful function as a lazy IO.
       */
      fun <T> effectTotal(f: suspend () -> T): UIO<T> = EffectTotal(f)

      /**
       * Acquires a resource, uses that resource, with a guaranteed release operation.
       */
      fun <T, U> bracket(acquire: () -> T, use: (T) -> U, release: (T) -> Unit): Task<U> =
         Bracket(acquire, use, release)
   }

   fun <U> map(f: (T) -> U): IO<E, U> = MapFn(f, this)

   fun <F> mapError(f: (E) -> F): IO<F, T> = MapErrorFn(f, this)

   /**
    * Provides a context switch for this IO.
    */
   fun context(context: CoroutineContext): IO<E, T> = WithContext(this, context)

   /**
    * Executes this IO.
    */
   suspend fun run(): Either<E, T> = this@IO.apply()

   /**
    * Executes this IO, using the supplied dispatcher as the context.
    * Shorthand for `context(dispatcher).run()`
    */
   suspend fun runOn(dispatcher: CoroutineDispatcher): Either<E, T> {
      return withContext(dispatcher) {
         this@IO.apply()
      }
   }
}

fun <E, T> IO<E, T>.timeout(millis: Long, ifError: (TimeoutCancellationException) -> E): IO<E, T> =
   IO.WithTimeout(millis, ifError, this)

fun <E, F : E, T> IO<E, T>.refineOrDie(): IO<F, T> = TODO()

fun <E, T, U> IO<E, T>.flatMap(f: (T) -> IO<E, U>): IO<E, U> = TODO()


typealias UIO<T> = IO<Nothing, T>         // Succeed with an `T`, cannot fail
typealias Task<T> = IO<Throwable, T>      // Succeed with an `T`, may fail with `Throwable`
typealias Failed<E> = IO<E, Nothing>      // Cannot suceed
