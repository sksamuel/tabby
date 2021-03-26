package com.sksamuel.tabby.`try`

fun <A> List<Try<A>>.filterToSuccesses(): List<Try.Success<A>> =
   filterIsInstance<Try.Success<A>>()

fun <A> List<Try<A>>.filterToFailures(): List<Try.Failure> =
   filterIsInstance<Try.Failure>()
