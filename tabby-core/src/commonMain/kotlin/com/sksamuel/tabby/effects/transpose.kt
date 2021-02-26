package com.sksamuel.tabby.effects

fun <A> IO<A>?.transpose(): IO<A?> = this ?: IO.pure(null)
