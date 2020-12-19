package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.left
import com.sksamuel.tabby.either.right
import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.toOption

/**
 * Wraps the successful result of this IO in an option, such that IO<A> becomes IO<Option<A>>.
 */
fun <A> IO<A>.optional(): IO<Option<A>> = object : IO<Option<A>>() {
   override suspend fun apply(): Try<Option<A>> = this@optional.apply().map { it.toOption() }
}

/**
 * Applies the given function [f] to the successful result of this IO.
 * This is the equivalent of mapping over this IO with a nested map on the contained option.
 */
fun <A, B> IO<Option<A>>.mapValue(f: (A) -> B): IO<Option<B>> {
   return this.map { it.map(f) }
}


/**
 * Applies the given function [f] to the successful result of this IO.
 * This is the equivalent of mapping over this IO with a nested flatMap on the contained option.
 */
fun <A, B> IO<Option<A>>.flatMapValue(f: (A) -> Option<B>): IO<Option<B>> {
   return this.map { it.flatMap(f) }
}

/**
 * Collapses the value of this IO, if it is empty, into an error state of the IO itself.
 * In other words, IO<Some<A>> would be absolved to IO<A> and IO<None> will result in a failed IO.
 *
 * The given function [ifNone] is used to generate the failure value in the case of an empty result.
 */
fun <A> IO<Option<A>>.absolve(ifNone: () -> Throwable = { NoSuchElementException() }): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> {
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
