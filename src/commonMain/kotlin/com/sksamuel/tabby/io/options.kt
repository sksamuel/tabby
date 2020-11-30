package com.sksamuel.tabby.io

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.flatMap
import com.sksamuel.tabby.either.left
import com.sksamuel.tabby.either.right
import com.sksamuel.tabby.option.Option
import kotlin.jvm.JvmName

/**
 * Applies the given function [f] to the successful result of this IO if that successful
 * value is a [Option.None]. The result of invoking [f] is an effect which is then executed
 * and flatted into the result.
 *
 * This is similar to Option.orElse, except handles the orElse case for options wrapped in [IO]s.
 */
fun <E, T> IO<E, Option<T>>.orElse(f: () -> IO<E, T>): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> {
      return this@orElse.run().flatMap { result ->
         result.fold(
            { f().run() },
            { it.right() }
         )
      }
   }
}

/**
 * Returns an effect which flattens this effect, mapping a successful result of none
 * to a failed IO of [NoSuchElementException], or mapping a successful result of some to an IO<A>.
 */
@JvmName("flattenOption")
fun <A> Task<Option<A>>.flatten(): Task<A> = object : Task<A>() {
   override suspend fun apply(): Either<Throwable, A> {
      return this@flatten.run()
         .flatMap { result -> result.fold({ NoSuchElementException().left() }, { it.right() }) }
   }
}

@JvmName("absolveOption")
@Deprecated("use flatten")
fun <T> Task<Option<T>>.absolve(): Task<T> = object : Task<T>() {
   override suspend fun apply(): Either<Throwable, T> {
      return this@absolve.apply().fold(
         { it.left() },
         { right ->
            right.fold(
               { NoSuchElementException().left() },
               { it.right() }
            )
         }
      )
   }
}

fun <E, T> IO<E, Option<T>>.absolve(ifNone: () -> E): IO<E, T> = object : IO<E, T>() {
   override suspend fun apply(): Either<E, T> {
      return this@absolve.apply().fold(
         { it.left() },
         { right ->
            right.fold(
               { ifNone().left() },
               { it.right() }
            )
         }
      )
   }
}

/**
 * If this IO is successful, will apply the given function [f] to the result of this IO, if it is a [Option.Some].
 * If the successful result is a none, then it will returned as is.
 */
fun <E, T, U> IO<E, Option<T>>.mapSome(f: (T) -> U): IO<E, Option<U>> {
   return this.map { it.map(f) }
}

/**
 * If this IO is successful, will apply the given function [f] to the result of this IO, if it is a [Option.Some].
 * If the successful result is a none, then it will returned as is.
 */
fun <E, T, U> IO<E, Option<T>>.flatMapSome(f: (T) -> Option<U>): IO<E, Option<U>> {
   return this.map { it.flatMap(f) }
}
