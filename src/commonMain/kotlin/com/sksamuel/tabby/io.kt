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
abstract class IO<out E, out T> {

   abstract suspend fun apply(): Either<E, T>

   class Pure<T>(private val t: T) : UIO<T>() {
      override suspend fun apply() = t.right()
   }

   class Failure<T>(private val t: T) : FIO<T>() {
      override suspend fun apply() = t.left()
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

   class FlatMap<E, T, U>(val f: (T) -> IO<E, U>, private val underlying: IO<E, T>) : IO<E, U>() {
      override suspend fun apply(): Either<E, U> = underlying.apply().flatMap { f(it).apply() }
   }

   class MapErrorFn<E, T, E2>(private val f: (E) -> E2, private val underlying: IO<E, T>) : IO<E2, T>() {
      override suspend fun apply() = underlying.apply().mapLeft(f)
   }

   class FlatMapErrorFn<E, T, E2>(private val f: (E) -> FIO<E2>, private val underlying: IO<E, T>) : IO<E2, T>() {
      override suspend fun apply() = underlying.apply().flatMapLeft { f(it).apply() }
   }

   class WithContext<E, T>(private val io: IO<E, T>, private val context: CoroutineContext) : IO<E, T>() {
      override suspend fun apply(): Either<E, T> = withContext(context) { io.apply() }
   }

   class Zip<E, T, U, V>(private val left: IO<E, T>,
                         private val right: IO<E, U>,
                         private val f: (T, U) -> V) : IO<E, V>() {
      override suspend fun apply(): Either<E, V> {
         return left.apply().flatMap { t ->
            right.apply().map { u ->
               f(t, u)
            }
         }
      }
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
      fun <E> failure(e: E): FIO<E> = Failure(e)

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

   fun <U> map(f: (T) -> U): IO<E, U> = FlatMap({ f(it).pure() }, this)

   fun <E2> mapError(f: (E) -> E2): IO<E2, T> = MapErrorFn(f, this)

   fun <E2> flatMapError(f: (E) -> FIO<E2>): IO<E2, T> = FlatMapErrorFn(f, this)

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

fun <T> T.pure(): UIO<T> = IO.pure(this)

fun <E, T, U, V> IO<E, T>.zip(other: IO<E, U>, f: (T, U) -> V): IO<E, V> = IO.Zip(this, other, f)

fun <E, T> IO<E, T>.timeout(millis: Long, ifError: (TimeoutCancellationException) -> E): IO<E, T> =
   IO.WithTimeout(millis, ifError, this)

fun <E, E2, T> IO<E, T>.refineOrDie(): IO<E2, T> = TODO()

fun <E, T, U> IO<E, T>.flatMap(f: (T) -> IO<E, U>): IO<E, U> = IO.FlatMap(f, this)

// Infallible IO, will never fail
typealias UIO<T> = IO<Nothing, T>         // Succeed with an `T`, cannot fail
typealias Task<T> = IO<Throwable, T>      // Succeed with an `T`, may fail with `Throwable`

// Unproductive IO, will never succeed
typealias FIO<E> = IO<E, Nothing>      // Cannot succeed
