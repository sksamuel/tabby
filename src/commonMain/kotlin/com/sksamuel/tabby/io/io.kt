package com.sksamuel.tabby.io

import com.sksamuel.tabby.Either
import com.sksamuel.tabby.either
import com.sksamuel.tabby.flatMap
import com.sksamuel.tabby.flatMapLeft
import com.sksamuel.tabby.flatten
import com.sksamuel.tabby.left
import com.sksamuel.tabby.right
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

   class Succeeded<T>(private val t: T) : UIO<T>() {
      override suspend fun apply() = t.right()
   }

   class Success<T>(private val f: () -> T) : IO<Nothing, T>() {
      override suspend fun apply() = f().right()
   }

   class Failed<E>(private val error: E) : FIO<E>() {
      override suspend fun apply() = error.left()
   }

   class Failure<E>(private val f: () -> E) : FIO<E>() {
      override suspend fun apply() = f().left()
   }

   class Effect<T>(private val f: suspend () -> T) : IO<Throwable, T>() {
      override suspend fun apply() = either { f() }
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

   /**
    * Surrounds this IO with a before and after operation.
    * If before fails, after will not be invoked.
    * If this IO fails, after will be invoked but its return value will be ignored.
    */
   fun <E, T, A> IO<E, T>.brace(before: IO<E, A>, after: (A) -> IO<E, Unit>): IO<E, T> = object : IO<E, T>() {
      override suspend fun apply(): Either<E, T> {
         return before.apply().flatMap { a ->
            this@brace.apply().fold(
               {
                  after(a)
                  it.left()
               },
               { t ->
                  after(a).apply().map { t }
               }
            )
         }
      }
   }

   companion object {

      /**
       * Wraps a strict value as a successfully completed IO.
       */
      fun <T> success(t: T): UIO<T> = Succeeded(t)

      /**
       * Wraps a function as a successfully completed IO.
       */
      fun <T> success(f: () -> T): UIO<T> = Success(f)

      /**
       * Wraps a strict value as a failed IO.
       */
      fun <E> failure(e: E): FIO<E> = Failed(e)

      /**
       * Wraps a function as a failed IO.
       */
      fun <E> failure(f: () -> E): FIO<E> = Failure(f)

      /**
       * Wraps a potentially throwing effectful function as a lazy IO.
       */
      fun <T> effect(f: suspend () -> T): Task<T> = Effect(f)

      /**
       * Wraps an infallible effectful function as a lazy IO.
       */
      fun <T> effectTotal(f: suspend () -> T): UIO<T> = EffectTotal(f)

      /**
       * Acquires a resource, uses that resource, with a guaranteed release operation.
       */
      fun <T, U> bracket(acquire: () -> T, use: (T) -> U, release: (T) -> Unit): Task<U> =
         Bracket(acquire, use, release)

      fun <E, T> par(vararg ios: IO<E, T>): IO<E, List<T>> = object : IO<E, List<T>>() {
         override suspend fun apply(): Either<E, List<T>> {
            return try {
               coroutineScope {
                  val ds = ios.map {
                     async {
                        it.run().fold(
                           { throw FailedIO(it) },
                           { it }
                        )
                     }
                  }
                  ds.awaitAll().right()
               }
            } catch (e: FailedIO) {
               (e.error as E).left()
            }
         }
      }
   }

   class FailedIO(val error: Any?) : RuntimeException()

   fun <U> map(f: (T) -> U): IO<E, U> = FlatMap({ f(it).success() }, this)

   fun <E2> mapError(f: (E) -> E2): IO<E2, T> = MapErrorFn(f, this)

   fun <E2> flatMapError(f: (E) -> FIO<E2>): IO<E2, T> = FlatMapErrorFn(f, this)

   fun swap(): IO<T, E> = object : IO<T, E>() {
      override suspend fun apply(): Either<T, E> = this@IO.apply().swap()
   }

   /**
    * Provides a context switch for this IO.
    */
   fun onContext(context: CoroutineContext): IO<E, T> = WithContext(this, context)

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

fun <E, T> IO<E, Either<E, T>>.flatten(): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> {
      return this@flatten.apply().flatten()
   }
}

fun <A> IO<A, A>.fail(): IO<A, Nothing> = fail { it }

/**
 * Coalesces an IO<E,T> to an FIO<E> using the supplied function to convert a success to a failure.
 */
fun <E, T> IO<E, T>.fail(ifSuccess: (T) -> E): FIO<E> = object : IO<E, Nothing>() {
   override suspend fun apply(): Either<E, Nothing> {
      return this@fail.apply().fold(
         { it.left() },
         { ifSuccess(it).left() }
      )
   }
}

fun <T> T.success(): UIO<T> = IO.success(this)

fun <E, T, U, V> IO<E, T>.zip(other: IO<E, U>, f: (T, U) -> V): IO<E, V> = IO.Zip(this, other, f)

fun <E, U, V> IO<E, *>.zipRight(other: IO<E, U>, f: (U) -> V): IO<E, V> = object : IO<E, V>() {
   override suspend fun apply(): Either<E, V> = this@zipRight.apply().flatMap {
      other.apply().map(f)
   }
}

fun <E, T, V> IO<E, T>.zipLeft(other: IO<E, *>, f: (T) -> V): IO<E, V> = object : IO<E, V>() {
   override suspend fun apply(): Either<E, V> = this@zipLeft.apply().flatMap { t ->
      other.apply().map { f(t) }
   }
}

fun <E, T> IO<E, T>.timeout(millis: Long, error: E): IO<E, T> =
   IO.WithTimeout(millis, { error }, this)

fun <E, T> IO<E, T>.timeout(millis: Long, ifError: (TimeoutCancellationException) -> E): IO<E, T> =
   IO.WithTimeout(millis, ifError, this)

inline fun <reified E> IO<*, *>.refineOrDie(): FIO<E> = object : FIO<E>() {
   override suspend fun apply(): Either<E, Nothing> {
      return this@refineOrDie.apply().fold(
         { if (it is E) it.left() else error("Result not an instance of ${E::class}") },
         { error("Result not an instance of ${E::class}") }
      )
   }
}

fun <E, T, U> IO<E, T>.flatMap(f: (T) -> IO<E, U>): IO<E, U> = IO.FlatMap(f, this)

// Infallible IO, will never fail
typealias UIO<T> = IO<Nothing, T>         // Succeed with an `T`, cannot fail
typealias Task<T> = IO<Throwable, T>      // Succeed with an `T`, may fail with `Throwable`

// Unproductive IO, will never succeed
typealias FIO<E> = IO<E, Nothing>      // Cannot succeed
