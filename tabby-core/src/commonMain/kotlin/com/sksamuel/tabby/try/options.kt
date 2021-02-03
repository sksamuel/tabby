package com.sksamuel.tabby.`try`

import com.sksamuel.tabby.option.Option

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
