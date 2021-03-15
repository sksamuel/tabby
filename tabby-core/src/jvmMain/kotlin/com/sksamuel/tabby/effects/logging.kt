package com.sksamuel.tabby.effects

import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 * If this Try is failure, will log at the error level the contained throwable and a message
 * generated from the given function.
 */
fun <A> IO<A>.logError(messageFn: (Throwable) -> String) {
   this.onError { logger.error(it) { messageFn(it) } }
}
