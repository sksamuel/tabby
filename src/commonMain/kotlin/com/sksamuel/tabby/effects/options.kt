package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.toOption

/**
 * Wraps the successful result of this IO in an option.
 */
fun <A> IO<A>.optional(): IO<Option<A>> = object : IO<Option<A>>() {
   override suspend fun apply(): Try<Option<A>> = this@optional.apply().map { it.toOption() }
}
