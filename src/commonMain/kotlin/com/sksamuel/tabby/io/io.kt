@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.sksamuel.tabby.io

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.Tuple2
import com.sksamuel.tabby.Tuple3
import com.sksamuel.tabby.Tuple4
import com.sksamuel.tabby.either.either
import com.sksamuel.tabby.either.filter
import com.sksamuel.tabby.either.flatMap
import com.sksamuel.tabby.either.flatMapLeft
import com.sksamuel.tabby.either.flatten
import com.sksamuel.tabby.either.left
import com.sksamuel.tabby.option.none
import com.sksamuel.tabby.either.right
import com.sksamuel.tabby.option.some
import com.sksamuel.tabby.option.toOption
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

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

   class EffectWith<E, T>(private val f: suspend () -> Either<E, T>) : IO<E, T>() {
      override suspend fun apply() = f()
   }

   class EffectTotal<T>(private val f: suspend () -> T) : UIO<T>() {
      override suspend fun apply() = f().right()
   }

   class OnError<E, T>(
      private val effect: IO<E, T>,
      private val onError: (E) -> Unit,
   ) : IO<E, T>() {
      override suspend fun apply(): Either<E, T> = effect.apply().onLeft(onError)
   }

   class WithTimeout<E, T>(
      private val duration: Long,
      private val ifError: (TimeoutCancellationException) -> E,
      private val underlying: IO<E, T>,
   ) : IO<E, T>() {
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

   class WrapEither<E, T>(private val either: Either<E, T>) : IO<E, T>() {
      override suspend fun apply(): Either<E, T> = either
   }

   class CollectSuccess<E, T>(private val effects: List<IO<E, T>>) : UIO<List<T>>() {
      override suspend fun apply(): Either<Nothing, List<T>> {
         return effects.fold(emptyList<T>()) { acc, op ->
            op.apply().fold({ acc }, { acc + it })
         }.right()
      }
   }

   class FailedIO(val error: Any?) : RuntimeException()

   class Zip<E, T, U, V>(
      private val left: IO<E, T>,
      private val right: IO<E, U>,
      private val f: (T, U) -> V,
   ) : IO<E, V>() {
      override suspend fun apply(): Either<E, V> {
         return left.apply().flatMap { t ->
            right.apply().map { u ->
               f(t, u)
            }
         }
      }
   }

   class Bracket<T, U>(
      private val acquire: () -> T,
      private val use: (T) -> U,
      private val release: (T) -> Unit,
   ) : Task<U>() {
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

   /**
    * Returns an effect that runs the safe, side effecting function on the success of this effect.
    */
   fun forEach(f: suspend (T) -> Unit): IO<E, T> = object : IO<E, T>() {
      override suspend fun apply(): Either<E, T> = this@IO.apply().onRight { f(it) }
   }

   /**
    * Alias for forEach
    */
   fun onSuccess(f: suspend (T) -> Unit): IO<E, T> = forEach(f)

   companion object {

      val unit = pure(Unit)

      val empty = pure(none)

      /**
       * Wraps a strict value as a successfully completed IO.
       */
      fun <T> success(t: T): UIO<T> = Succeeded(t)

      // synonym for success
      fun <T> pure(t: T): UIO<T> = success(t)

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
       * Returns a failed IO with the given message converted to a runtime exception.
       */
      fun fail(msg: String): FIO<Throwable> = Failure { RuntimeException(msg) }

      /**
       * Wraps a potentially throwing effectful function as an IO.
       */
      fun <T> effect(f: suspend () -> T): Task<T> = Effect(f)

      /**
       * Wraps a potentially throwing effectful error handling function as an IO.
       */
      fun <E, T> effectWith(f: suspend () -> Either<E, T>): IO<E, T> = EffectWith(f)

      /**
       * Wraps an infallible effectful function as a lazy IO.
       */
      fun <T> effectTotal(f: suspend () -> T): UIO<T> = EffectTotal(f)

      /**
       * Evaluate the predicate, returning T as success if the predicate is true,
       * returning E as other otherwise.
       */
      fun <E, T> cond(predicate: Boolean, success: () -> T, error: () -> E): IO<E, T> =
         if (predicate) IO.success(success()) else failure(error())

      /**
       * Evaluate the predicate fn, returning T as success if the predicate is true,
       * returning E as other otherwise.
       */
      fun <E, T> cond(predicate: () -> Boolean, success: () -> T, error: () -> E): IO<E, T> =
         if (predicate()) IO.success(success()) else failure(error())

      /**
       * Returns an effect that contains the results of the given effects. If any of the given
       * effects fails, this effect will fail and any successes will be dropped.
       */
      fun <E, T> traverse(vararg effects: IO<E, T>): IO<E, List<T>> = traverse(effects.asList())

      fun <E, T> traverse(effects: List<IO<E, T>>): IO<E, List<T>> = object : IO<E, List<T>>() {
         override suspend fun apply(): Either<E, List<T>> {
            return effects.fold(emptyList<T>()) { acc, effect ->
               when (val result = effect.apply()) {
                  is Either.Left -> return result
                  is Either.Right -> acc + result.b
               }
            }.right()
         }
      }

      /**
       * Evaluate and run each effect in the structure, in sequence,
       * and collect discarding failed ones.
       */
      fun <E, T> collectSuccess(vararg effects: IO<E, T>): Task<List<T>> = CollectSuccess(effects.asList())

      fun <E, T> collectSuccess(effects: List<IO<E, T>>): Task<List<T>> = CollectSuccess(effects)

      fun <E, T> either(either: Either<E, T>) = WrapEither(either)

      /**
       * Acquires a resource, uses that resource, with a guaranteed release operation.
       */
      fun <T, U> bracket(acquire: () -> T, use: (T) -> U, release: (T) -> Unit): Task<U> =
         Bracket(acquire, use, release)

      /**
       * Reduces IOs using the supplied function, working sequentially, or returns the
       * first failure.
       */
      fun <E, T> reduce(first: IO<E, T>, vararg rest: IO<E, T>, f: (T, T) -> T): IO<E, T> = object : IO<E, T>() {
         override suspend fun apply(): Either<E, T> {
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

      fun <E, A, B, R> mapN(first: IO<E, A>, second: IO<E, B>, f: (A, B) -> R): IO<E, R> = object : IO<E, R>() {
         override suspend fun apply(): Either<E, R> {
            return first.apply().flatMap { a -> second.apply().map { b -> f(a, b) } }
         }
      }

      fun <E, A, B, C, R> mapN(
         first: IO<E, A>,
         second: IO<E, B>,
         third: IO<E, C>,
         f: (A, B, C) -> R,
      ): IO<E, R> = object : IO<E, R>() {
         override suspend fun apply(): Either<E, R> {
            return first.apply().flatMap { a ->
               second.apply().flatMap { b ->
                  third.apply().map { c ->
                     f(a, b, c)
                  }
               }
            }
         }
      }

      fun <E, A, B, C, D, R> mapN(
         first: IO<E, A>,
         second: IO<E, B>,
         third: IO<E, C>,
         fourth: IO<E, D>,
         f: (A, B, C, D) -> R,
      ): IO<E, R> = object : IO<E, R>() {
         override suspend fun apply(): Either<E, R> {
            return first.apply().flatMap { a ->
               second.apply().flatMap { b ->
                  third.apply().flatMap { c ->
                     fourth.apply().map { d ->
                        f(a, b, c, d)
                     }
                  }
               }
            }
         }
      }

      fun <E, A, B> parN(ioa: IO<E, A>, iob: IO<E, B>) = object : IO<E, Tuple2<A, B>>() {
         override suspend fun apply(): Either<E, Tuple2<A, B>> {
            return try {
               coroutineScope {
                  val a = async {
                     ioa.run().fold(
                        { throw FailedIO(it) },
                        { it }
                     )
                  }
                  val b = async {
                     iob.run().fold(
                        { throw FailedIO(it) },
                        { it }
                     )
                  }
                  Tuple2(a.await(), b.await()).right()
               }
            } catch (e: FailedIO) {
               (e.error as E).left()
            }
         }
      }

      fun <E, A, B, C> parN(ioa: IO<E, A>, iob: IO<E, B>, ioc: IO<E, C>) = object : IO<E, Tuple3<A, B, C>>() {
         override suspend fun apply(): Either<E, Tuple3<A, B, C>> {
            return try {
               coroutineScope {
                  val a = async {
                     ioa.run().fold(
                        { throw FailedIO(it) },
                        { it }
                     )
                  }
                  val b = async {
                     iob.run().fold(
                        { throw FailedIO(it) },
                        { it }
                     )
                  }
                  val c = async {
                     ioc.run().fold(
                        { throw FailedIO(it) },
                        { it }
                     )
                  }
                  Tuple3(a.await(), b.await(), c.await()).right()
               }
            } catch (e: FailedIO) {
               (e.error as E).left()
            }
         }
      }

      fun <E, A, B, C, D> parN(
         ioa: IO<E, A>,
         iob: IO<E, B>,
         ioc: IO<E, C>,
         iod: IO<E, D>,
      ) = object : IO<E, Tuple4<A, B, C, D>>() {
         override suspend fun apply(): Either<E, Tuple4<A, B, C, D>> {
            return try {
               coroutineScope {
                  val a = async {
                     ioa.run().fold(
                        { throw FailedIO(it) },
                        { it }
                     )
                  }
                  val b = async {
                     iob.run().fold(
                        { throw FailedIO(it) },
                        { it }
                     )
                  }
                  val c = async {
                     ioc.run().fold(
                        { throw FailedIO(it) },
                        { it }
                     )
                  }
                  val d = async {
                     iod.run().fold(
                        { throw FailedIO(it) },
                        { it }
                     )
                  }
                  Tuple4(a.await(), b.await(), c.await(), d.await()).right()
               }
            } catch (e: FailedIO) {
               (e.error as E).left()
            }
         }
      }

      fun <E, T> par(effects: List<IO<E, T>>): IO<E, List<T>> = object : IO<E, List<T>>() {
         override suspend fun apply(): Either<E, List<T>> {
            return try {
               coroutineScope {
                  val ds = effects.map {
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

      fun <E, T> par(vararg effects: IO<E, T>): IO<E, List<T>> = par(effects.asList())
   }


   fun <U> map(f: (T) -> U): IO<E, U> = FlatMap({ f(it).success() }, this)

   /**
    * Executes the given function if this is an error.
    * Returns this
    */
   fun onError(f: (E) -> Unit): IO<E, T> = OnError(this, f)

   fun <K> trace(before: suspend () -> K, after: suspend (K) -> Unit): IO<E, T> = object : IO<E, T>() {
      override suspend fun apply(): Either<E, T> {
         val k = try {
            before()
         } catch (t: Throwable) {
            null
         }
         val result = this@IO.apply()
         try {
            if (k != null) after(k)
         } catch (t: Throwable) {
         }
         return result
      }
   }

   /**
    * Wraps this IO in a synchronization operation that will ensure the effect
    * only takes place once a permit is acquired from the given semaphore.
    *
    * While waiting to acquire, the effect will suspend.
    */
   fun synchronize(semaphore: Semaphore): IO<E, T> = object : IO<E, T>() {
      override suspend fun apply(): Either<E, T> =
         semaphore.withPermit { this@IO.apply() }
   }

   fun <E2> mapError(f: (E) -> E2): IO<E2, T> = MapErrorFn(f, this)

   fun <E2> flatMapError(f: (E) -> FIO<E2>): IO<E2, T> = FlatMapErrorFn(f, this)

   fun swap(): IO<T, E> = object : IO<T, E>() {
      override suspend fun apply(): Either<T, E> = this@IO.apply().swap()
   }

   fun onEach(ifError: (E) -> Unit, ifSuccess: (T) -> Unit): IO<E, T> = object : IO<E, T>() {
      override suspend fun apply(): Either<E, T> {
         return this@IO.run().onLeft { ifError(it) }.onRight { ifSuccess(it) }
      }
   }

   /**
    * Provides a context switch for this IO.
    */
   fun onContext(context: CoroutineContext): IO<E, T> = WithContext(this, context)

   /**
    * Executes this IO, returning the result as an [Either].
    */
   suspend fun run(): Either<E, T> = this@IO.apply()

   suspend fun runUnsafe(): T = run().getRightUnsafe()

   /**
    * Returns a new IO which is just this IO but with the result of a successful execution
    * replaced with the given strict value.
    */
   fun <B> with(b: B): IO<E, B> = object : IO<E, B>() {
      override suspend fun apply(): Either<E, B> = this@IO.run().map { b }
   }

   /**
    * Executes this IO, with the calling coroutine as the context.
    * Returns the successful result or null.
    */
   suspend fun runOrNull(): T? = run().getRightOrNull()

   /**
    * Executes this IO, with the calling coroutine as the context.
    * Returns the successful result wrapped in an option, or none.
    */
   suspend fun runOrNone(): Option<T> = run().toOption()

   /**
    * Executes this IO, using the supplied dispatcher as the context.
    * Shorthand for `context(dispatcher).run()`
    */
   suspend fun runOn(dispatcher: CoroutineDispatcher): Either<E, T> {
      return withContext(dispatcher) {
         this@IO.apply()
      }
   }

   /**
    * Ignores any success value, returning an effect that producess Unit.
    */
   fun unit(): IO<E, Unit> = object : IO<E, Unit>() {
      override suspend fun apply(): Either<E, Unit> = this@IO.apply().map { Unit }
   }

   /**
    * Returns a memoized version of this effect.
    */
   fun memoize(): IO<E, T> = object : IO<E, T>() {
      var memoized: Any? = null
      override suspend fun apply(): Either<E, T> {
         if (memoized == null) {
            memoized = this@IO.apply()
         }
         return memoized as Either<E, T>
      }
   }

   /**
    * Wraps the successful result of this IO in an option.
    */
   fun optional(): IO<E, Option<T>> = object : IO<E, Option<T>>() {
      override suspend fun apply(): Either<E, Option<T>> = this@IO.apply().map { it.toOption() }
   }

   /**
    * Returns a memoized version of this effect that will hold the memoized value for the specified duration.
    */
   @OptIn(ExperimentalTime::class)
   fun memoizeFor(duration: Duration): IO<E, T> = object : IO<E, T>() {
      var memoized: Any? = null
      var mark = TimeSource.Monotonic.markNow().plus(duration)
      override suspend fun apply(): Either<E, T> {
         if (memoized == null || mark.hasPassedNow()) {
            memoized = this@IO.apply()
            mark = TimeSource.Monotonic.markNow().plus(duration)
         }
         return memoized as Either<E, T>
      }
   }
}

fun <A> IO<A, A>.merge(): UIO<A> = object : UIO<A>() {
   override suspend fun apply(): Either<Nothing, A> = this@merge.apply().fold({ it.right() }, { it.right() })
}

fun <E, A, B, R> IO<E, A>.mapN(other: IO<E, B>, f: (A, B) -> R): IO<E, R> = IO.mapN(this, other, f)

fun <E, A, B, C, R> IO<E, A>.mapN(second: IO<E, B>, third: IO<E, C>, f: (A, B, C) -> R): IO<E, R> =
   IO.mapN(this, second, third, f)

fun <E, A, B, C, D, R> IO<E, A>.mapN(
   second: IO<E, B>,
   third: IO<E, C>,
   fourth: IO<E, D>,
   f: (A, B, C, D) -> R,
): IO<E, R> =
   IO.mapN(this, second, third, fourth, f)

/**
 * Applies the given effectful function [f] to the successful result of this IO if that
 * result is a None. This is the IO equivalent of Option.orElse.
 */
fun <E, T> IO<E, Option<T>>.orElseOption(f: () -> IO<E, Option<T>>): IO<E, Option<T>> = object : IO<E, Option<T>>() {
   override suspend fun apply(): Either<E, Option<T>> {
      return this@orElseOption.run().flatMap { result ->
         result.fold(
            { f().run() },
            { it.some().right() }
         )
      }
   }
}

/**
 * Fails with [elseIf] if the predicate fails.
 */
fun <E, T> IO<E, T>.filterOrFail(p: (T) -> Boolean, elseIf: (T) -> E): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> = this@filterOrFail.apply().filter(p, elseIf)
}

suspend fun <T> Task<T>.runOrThrow() = run().fold({ throw it }, { it })

/**
 * Recovers from an error by using the given function.
 */
fun <E, T> IO<E, T>.recover(f: (E) -> T): IO<E, T> = object : UIO<T>() {
   override suspend fun apply(): Either<Nothing, T> = this@recover.apply().fold({ f(it).right() }, { it.right() })
}

fun <E, T> IO<E, T>.recoverWith(f: (E) -> IO<E, T>): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> = this@recoverWith.apply().fold({ f(it).apply() }, { it.right() })
}

@Deprecated("This will be renamed when I think of a better name")
fun <E, T> IO<E, Either<E, T>>.pull(): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> {
      return this@pull.apply().flatten()
   }
}

fun <E, T> IO<E, IO<E, T>>.flatten(): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> {
      return this@flatten.apply().flatMap { it.apply() }
   }
}

fun <A> IO<A, A>.fail(): IO<A, Nothing> = fail { it }

/**
 * Returns an effect that peeks at the success of this effect.
 * The result of the tap function is ignored.
 */
fun <E, T> IO<E, T>.tap(f: (T) -> IO<*, *>): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> {
      return this@tap.apply().onRight { f(it).run() }
   }
}

/**
 * Returns an effect that peeks at the failure of this effect.
 * The result of the tap function is ignored.
 */
fun <E, T> IO<E, T>.tapError(f: (E) -> IO<*, *>): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> {
      return this@tapError.apply().onLeft { f(it).run() }
   }
}

/**
 * Returns an effect which runs the given effect if this effect evaluates to true.
 * Returns the this effect.
 */
fun <E, T> IO<E, T>.then(f: () -> IO<*, *>): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> {
      return this@then.apply().onRight { f().run() }
   }
}

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
fun <E> E.failure(): FIO<E> = IO.failure(this)

fun <E, A, B> IO<E, A>.zip(other: IO<E, B>): IO<E, Pair<A, B>> = object : IO<E, Pair<A, B>>() {
   override suspend fun apply(): Either<E, Pair<A, B>> {
      return this@zip.apply().flatMap { a -> other.apply().map { b -> Pair(a, b) } }
   }
}

/**
 * For an IO<E, List<T>>, applies the given function to each element of the list,
 * returning a new IO with the mapped list.
 */
fun <E, T, U> IO<E, List<T>>.mapList(f: (T) -> U): IO<E, List<U>> {
   return this.map { it.map(f) }
}

@JvmName("absolveEither")
fun <E, T> IO<E, Either<E, T>>.absolve(): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> {
      return this@absolve.apply().fold(
         { it.left() },
         { right ->
            right.fold(
               { it.left() },
               { it.right() }
            )
         }
      )
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

/**
 * Returns an effect which applies the given side effecting function to it's success,
 * wrapping the supplied function in an effect before execution.
 *
 * In other words, this is a shorthand for the following.
 *
 * task.flatMap { IO.effect { f(it) } }
 */
fun <T, U> Task<T>.mapEffect(f: suspend (T) -> U): Task<U> = flatMap { t -> IO.effect { f(t) } }

internal val Nil = emptyList<Nothing>()

/**
 * Wraps an Either in an IO.
 */
fun <A, B> Either<A, B>.effect(): IO<A, B> = fold({ IO.failure(it) }, { IO.success(it) })

fun <E, T, U> IO<E, T>.flatMap(f: (T) -> IO<E, U>): IO<E, U> = IO.FlatMap(f, this)

// Infallible IO, will never fail
typealias UIO<T> = IO<Nothing, T>         // Succeed with an `T`, cannot fail
typealias Task<T> = IO<Throwable, T>      // Succeed with an `T`, may fail with `Throwable`

// Unproductive IO, will never succeed
typealias FIO<E> = IO<E, Nothing>      // Cannot succeed
