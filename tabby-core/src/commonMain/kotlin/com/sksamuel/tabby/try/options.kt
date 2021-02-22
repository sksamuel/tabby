package com.sksamuel.tabby.`try`

import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.none
import com.sksamuel.tabby.option.some

fun <A> Option<A>.toTry(message: String) = fold({ message.error() }, { it.value() })

fun <A> Try<Option<A>>.flatten(ifEmpty: () -> A): Try<A> = when (this) {
   is Try.Failure -> this
   is Try.Success -> value.fold({ ifEmpty().value() }, { it.value() })
}

fun <A> Try<Option<A>>.flatten(emptyMessage: String? = null): Try<A> = when (this) {
   is Try.Failure -> this
   is Try.Success -> when (val option = this.value) {
      is Option.Some -> Try.Success(option.value)
      is Option.None -> Try.Failure(NoSuchElementException(emptyMessage))
   }
}

/**
 * Acts as a monad transformer, converting an OptionTry into a TryOption.
 */
fun <A> Option<Try<A>>.transpose(): Try<Option<A>> =
   fold({ none.success() }, { t -> t.fold({ it.failure() }, { it.some().success() }) })

fun <A> Try<Option<A>>.transpose(): Option<Try<A>> =
   fold({ it.failure().some() }, { o -> o.fold({ none }, { it.success().some() }) })
