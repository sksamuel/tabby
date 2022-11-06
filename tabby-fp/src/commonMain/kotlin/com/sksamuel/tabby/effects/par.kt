package com.sksamuel.tabby.effects

import com.sksamuel.tabby.Tuple4
import com.sksamuel.tabby.Tuple5
import com.sksamuel.tabby.Tuple6
import com.sksamuel.tabby.Tuple7
import com.sksamuel.tabby.Tuple8
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

private val emptyEffect: () -> Result<Unit> = { Result.success(Unit) }

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
): Result<Pair<A, B>> =
   parN(effectA, effectB, emptyEffect, emptyEffect, emptyEffect, emptyEffect).map { Pair(it.a, it.b) }

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
): Result<Triple<A, B, C>> =
   parN(effectA, effectB, effectC, emptyEffect, emptyEffect, emptyEffect).map { Triple(it.a, it.b, it.c) }

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
): Result<Tuple4<A, B, C, D>> =
   parN(effectA, effectB, effectC, effectD, emptyEffect, emptyEffect).map { Tuple4(it.a, it.b, it.c, it.d) }

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
): Result<Tuple5<A, B, C, D, E>> =
   parN(effectA, effectB, effectC, effectD, effectE, emptyEffect).map { Tuple5(it.a, it.b, it.c, it.d, it.e) }

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
): Result<Tuple6<A, B, C, D, E, F>> =
   parN(effectA, effectB, effectC, effectD, effectE, effectF, emptyEffect, emptyEffect)
      .map { Tuple6(it.a, it.b, it.c, it.d, it.e, it.f) }

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
): Result<Tuple7<A, B, C, D, E, F, G>> =
   parN(effectA, effectB, effectC, effectD, effectE, effectF, effectG, emptyEffect)
      .map { Tuple7(it.a, it.b, it.c, it.d, it.e, it.f, it.g) }

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
): Result<Tuple8<A, B, C, D, E, F, G, H>> = runCatching {
   coroutineScope {
      val a = async {
         effectA().getOrThrow()
      }
      val b = async {
         effectB().getOrThrow()
      }
      val c = async {
         effectC().getOrThrow()
      }
      val d = async {
         effectD().getOrThrow()
      }
      val e = async {
         effectE().getOrThrow()
      }
      val f = async {
         effectF().getOrThrow()
      }
      val g = async {
         effectG().getOrThrow()
      }
      val h = async {
         effectH().getOrThrow()
      }
      awaitAll(a, b, c, d, e, f, g, h)
      Tuple8(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await())
   }
}
