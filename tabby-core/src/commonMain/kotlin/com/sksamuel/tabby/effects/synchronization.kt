package com.sksamuel.tabby.effects

import com.sksamuel.tabby.either.Try
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * Wraps this IO in a synchronization operation that will ensure the effect
 * only takes place once a permit is acquired from the given [semaphore].
 *
 * When waiting to acquire, the effect will suspend until a permit is available.
 */
fun <A> IO<A>.synchronize(semaphore: Semaphore): IO<A> = object : IO<A>() {
   override suspend fun apply(): Try<A> =
      semaphore.withPermit { this@synchronize.apply() }
}
