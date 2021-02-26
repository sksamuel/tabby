package com.sksamuel.tabby.nulls

import com.sksamuel.tabby.effects.IO

fun <A> IO<A>?.transpose(): IO<A?> = this ?: IO.pure(null)
