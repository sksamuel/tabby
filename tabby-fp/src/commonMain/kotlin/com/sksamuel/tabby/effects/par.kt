package com.sksamuel.tabby.effects

import com.sksamuel.tabby.Tuple4
import com.sksamuel.tabby.Tuple5
import com.sksamuel.tabby.Tuple6
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

private val emptyEffect: () -> Result<Unit> = { Result.success(Unit) }

suspend fun <A, B> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
): Result<Pair<A, B>> =
   parN(effectA, effectB, emptyEffect, emptyEffect, emptyEffect, emptyEffect).map { Pair(it.a, it.b) }

suspend fun <A, B, C> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
): Result<Triple<A, B, C>> =
   parN(effectA, effectB, effectC, emptyEffect, emptyEffect, emptyEffect).map { Triple(it.a, it.b, it.c) }

suspend fun <A, B, C, D> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
): Result<Tuple4<A, B, C, D>> =
   parN(effectA, effectB, effectC, effectD, emptyEffect, emptyEffect).map { Tuple4(it.a, it.b, it.c, it.d) }

suspend fun <A, B, C, D, E> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
): Result<Tuple5<A, B, C, D, E>> =
   parN(effectA, effectB, effectC, effectD, effectE, emptyEffect).map { Tuple5(it.a, it.b, it.c, it.d, it.e) }

suspend fun <A, B, C, D, E, F> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
): Result<Tuple6<A, B, C, D, E, F>> = runCatching {
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
      Tuple6(a.await(), b.await(), c.await(), d.await(), e.await(), f.await())
   }
}
