package com.sksamuel.tabby.`try`

fun <A> List<Try<A>>.filterToSuccesses(): List<A> =
   filterIsInstance<Try.Success<A>>().map { it.value }

fun <A> List<Try<A>>.filterToFailures(): List<Throwable> =
   filterIsInstance<Try.Failure>().map { it.error }
