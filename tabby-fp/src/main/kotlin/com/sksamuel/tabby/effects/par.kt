package com.sksamuel.tabby.effects

import com.sksamuel.tabby.Tuple10
import com.sksamuel.tabby.Tuple11
import com.sksamuel.tabby.Tuple12
import com.sksamuel.tabby.Tuple13
import com.sksamuel.tabby.Tuple14
import com.sksamuel.tabby.Tuple15
import com.sksamuel.tabby.Tuple16
import com.sksamuel.tabby.Tuple17
import com.sksamuel.tabby.Tuple18
import com.sksamuel.tabby.Tuple19
import com.sksamuel.tabby.Tuple20
import com.sksamuel.tabby.Tuple4
import com.sksamuel.tabby.Tuple5
import com.sksamuel.tabby.Tuple6
import com.sksamuel.tabby.Tuple7
import com.sksamuel.tabby.Tuple8
import com.sksamuel.tabby.Tuple9
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

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

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
): Result<Tuple9<A, B, C, D, E, F, G, H, I>> = runCatching {
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
      val i = async {
         effectI().getOrThrow()
      }
      awaitAll(a, b, c, d, e, f, g, h, i)
      Tuple9(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await(), i.await())
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
): Result<Tuple10<A, B, C, D, E, F, G, H, I, J>> = runCatching {
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
      val i = async {
         effectI().getOrThrow()
      }
      val j = async {
         effectJ().getOrThrow()
      }
      awaitAll(a, b, c, d, e, f, g, h, i, j)
      Tuple10(
         a.await(),
         b.await(),
         c.await(),
         d.await(),
         e.await(),
         f.await(),
         g.await(),
         h.await(),
         i.await(),
         j.await()
      )
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J, K> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
   effectK: suspend () -> Result<K>,
): Result<Tuple11<A, B, C, D, E, F, G, H, I, J, K>> = runCatching {
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
      val i = async {
         effectI().getOrThrow()
      }
      val j = async {
         effectJ().getOrThrow()
      }
      val k = async {
         effectK().getOrThrow()
      }
      awaitAll(a, b, c, d, e, f, g, h, i, j, k)
      Tuple11(
         a.await(),
         b.await(),
         c.await(),
         d.await(),
         e.await(),
         f.await(),
         g.await(),
         h.await(),
         i.await(),
         j.await(),
         k.await()
      )
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J, K, L> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
   effectK: suspend () -> Result<K>,
   effectL: suspend () -> Result<L>,
): Result<Tuple12<A, B, C, D, E, F, G, H, I, J, K, L>> = runCatching {
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
      val i = async {
         effectI().getOrThrow()
      }
      val j = async {
         effectJ().getOrThrow()
      }
      val k = async {
         effectK().getOrThrow()
      }
      val l = async {
         effectL().getOrThrow()
      }
      awaitAll(a, b, c, d, e, f, g, h, i, j, k, l)
      Tuple12(
         a.await(),
         b.await(),
         c.await(),
         d.await(),
         e.await(),
         f.await(),
         g.await(),
         h.await(),
         i.await(),
         j.await(),
         k.await(),
         l.await()
      )
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A> parN(vararg effects: suspend () -> Result<A>): Result<List<A>> = parN(effects.toList())

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A> parN(effects: List<suspend () -> Result<A>>): Result<List<A>> = runCatching {
   coroutineScope {
      val deferred = effects.map {
         async {
            it.invoke().getOrThrow()
         }
      }
      awaitAll(*deferred.toTypedArray())
   }
}

/**
 * Executes the given effects in parallel, failing fast, with at most [concurrent] effects running at once.
 */
suspend fun <A> parN(concurrent: Int, vararg effects: suspend () -> Result<A>): Result<List<A>> =
   parN(concurrent, effects.toList())

/**
 * Executes the given effects in parallel, failing fast, with at most [concurrent] effects running at once.
 */
suspend fun <A> parN(concurrent: Int, effects: List<suspend () -> Result<A>>): Result<List<A>> = runCatching {
   val semaphore = Semaphore(concurrent)
   coroutineScope {
      val deferred = effects.map {
         async {
            semaphore.withPermit {
               it.invoke().getOrThrow()
            }
         }
      }
      awaitAll(*deferred.toTypedArray())
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
   effectK: suspend () -> Result<K>,
   effectL: suspend () -> Result<L>,
   effectM: suspend () -> Result<M>,
): Result<Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M>> = runCatching {
   coroutineScope {
      val a = async { effectA().getOrThrow() }
      val b = async { effectB().getOrThrow() }
      val c = async { effectC().getOrThrow() }
      val d = async { effectD().getOrThrow() }
      val e = async { effectE().getOrThrow() }
      val f = async { effectF().getOrThrow() }
      val g = async { effectG().getOrThrow() }
      val h = async { effectH().getOrThrow() }
      val i = async { effectI().getOrThrow() }
      val j = async { effectJ().getOrThrow() }
      val k = async { effectK().getOrThrow() }
      val l = async { effectL().getOrThrow() }
      val m = async { effectM().getOrThrow() }
      awaitAll(a, b, c, d, e, f, g, h, i, j, k, l, m)
      Tuple13(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await(), i.await(), j.await(), k.await(), l.await(), m.await())
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
   effectK: suspend () -> Result<K>,
   effectL: suspend () -> Result<L>,
   effectM: suspend () -> Result<M>,
   effectN: suspend () -> Result<N>,
): Result<Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>> = runCatching {
   coroutineScope {
      val a = async { effectA().getOrThrow() }
      val b = async { effectB().getOrThrow() }
      val c = async { effectC().getOrThrow() }
      val d = async { effectD().getOrThrow() }
      val e = async { effectE().getOrThrow() }
      val f = async { effectF().getOrThrow() }
      val g = async { effectG().getOrThrow() }
      val h = async { effectH().getOrThrow() }
      val i = async { effectI().getOrThrow() }
      val j = async { effectJ().getOrThrow() }
      val k = async { effectK().getOrThrow() }
      val l = async { effectL().getOrThrow() }
      val m = async { effectM().getOrThrow() }
      val n = async { effectN().getOrThrow() }
      awaitAll(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
      Tuple14(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await(), i.await(), j.await(), k.await(), l.await(), m.await(), n.await())
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
   effectK: suspend () -> Result<K>,
   effectL: suspend () -> Result<L>,
   effectM: suspend () -> Result<M>,
   effectN: suspend () -> Result<N>,
   effectO: suspend () -> Result<O>,
): Result<Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>> = runCatching {
   coroutineScope {
      val a = async { effectA().getOrThrow() }
      val b = async { effectB().getOrThrow() }
      val c = async { effectC().getOrThrow() }
      val d = async { effectD().getOrThrow() }
      val e = async { effectE().getOrThrow() }
      val f = async { effectF().getOrThrow() }
      val g = async { effectG().getOrThrow() }
      val h = async { effectH().getOrThrow() }
      val i = async { effectI().getOrThrow() }
      val j = async { effectJ().getOrThrow() }
      val k = async { effectK().getOrThrow() }
      val l = async { effectL().getOrThrow() }
      val m = async { effectM().getOrThrow() }
      val n = async { effectN().getOrThrow() }
      val o = async { effectO().getOrThrow() }
      awaitAll(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
      Tuple15(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await(), i.await(), j.await(), k.await(), l.await(), m.await(), n.await(), o.await())
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
   effectK: suspend () -> Result<K>,
   effectL: suspend () -> Result<L>,
   effectM: suspend () -> Result<M>,
   effectN: suspend () -> Result<N>,
   effectO: suspend () -> Result<O>,
   effectP: suspend () -> Result<P>,
): Result<Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>> = runCatching {
   coroutineScope {
      val a = async { effectA().getOrThrow() }
      val b = async { effectB().getOrThrow() }
      val c = async { effectC().getOrThrow() }
      val d = async { effectD().getOrThrow() }
      val e = async { effectE().getOrThrow() }
      val f = async { effectF().getOrThrow() }
      val g = async { effectG().getOrThrow() }
      val h = async { effectH().getOrThrow() }
      val i = async { effectI().getOrThrow() }
      val j = async { effectJ().getOrThrow() }
      val k = async { effectK().getOrThrow() }
      val l = async { effectL().getOrThrow() }
      val m = async { effectM().getOrThrow() }
      val n = async { effectN().getOrThrow() }
      val o = async { effectO().getOrThrow() }
      val p = async { effectP().getOrThrow() }
      awaitAll(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
      Tuple16(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await(), i.await(), j.await(), k.await(), l.await(), m.await(), n.await(), o.await(), p.await())
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
   effectK: suspend () -> Result<K>,
   effectL: suspend () -> Result<L>,
   effectM: suspend () -> Result<M>,
   effectN: suspend () -> Result<N>,
   effectO: suspend () -> Result<O>,
   effectP: suspend () -> Result<P>,
   effectQ: suspend () -> Result<Q>,
): Result<Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>> = runCatching {
   coroutineScope {
      val a = async { effectA().getOrThrow() }
      val b = async { effectB().getOrThrow() }
      val c = async { effectC().getOrThrow() }
      val d = async { effectD().getOrThrow() }
      val e = async { effectE().getOrThrow() }
      val f = async { effectF().getOrThrow() }
      val g = async { effectG().getOrThrow() }
      val h = async { effectH().getOrThrow() }
      val i = async { effectI().getOrThrow() }
      val j = async { effectJ().getOrThrow() }
      val k = async { effectK().getOrThrow() }
      val l = async { effectL().getOrThrow() }
      val m = async { effectM().getOrThrow() }
      val n = async { effectN().getOrThrow() }
      val o = async { effectO().getOrThrow() }
      val p = async { effectP().getOrThrow() }
      val q = async { effectQ().getOrThrow() }
      awaitAll(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
      Tuple17(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await(), i.await(), j.await(), k.await(), l.await(), m.await(), n.await(), o.await(), p.await(), q.await())
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
   effectK: suspend () -> Result<K>,
   effectL: suspend () -> Result<L>,
   effectM: suspend () -> Result<M>,
   effectN: suspend () -> Result<N>,
   effectO: suspend () -> Result<O>,
   effectP: suspend () -> Result<P>,
   effectQ: suspend () -> Result<Q>,
   effectR: suspend () -> Result<R>,
): Result<Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>> = runCatching {
   coroutineScope {
      val a = async { effectA().getOrThrow() }
      val b = async { effectB().getOrThrow() }
      val c = async { effectC().getOrThrow() }
      val d = async { effectD().getOrThrow() }
      val e = async { effectE().getOrThrow() }
      val f = async { effectF().getOrThrow() }
      val g = async { effectG().getOrThrow() }
      val h = async { effectH().getOrThrow() }
      val i = async { effectI().getOrThrow() }
      val j = async { effectJ().getOrThrow() }
      val k = async { effectK().getOrThrow() }
      val l = async { effectL().getOrThrow() }
      val m = async { effectM().getOrThrow() }
      val n = async { effectN().getOrThrow() }
      val o = async { effectO().getOrThrow() }
      val p = async { effectP().getOrThrow() }
      val q = async { effectQ().getOrThrow() }
      val r = async { effectR().getOrThrow() }
      awaitAll(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
      Tuple18(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await(), i.await(), j.await(), k.await(), l.await(), m.await(), n.await(), o.await(), p.await(), q.await(), r.await())
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
   effectK: suspend () -> Result<K>,
   effectL: suspend () -> Result<L>,
   effectM: suspend () -> Result<M>,
   effectN: suspend () -> Result<N>,
   effectO: suspend () -> Result<O>,
   effectP: suspend () -> Result<P>,
   effectQ: suspend () -> Result<Q>,
   effectR: suspend () -> Result<R>,
   effectS: suspend () -> Result<S>,
): Result<Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>> = runCatching {
   coroutineScope {
      val a = async { effectA().getOrThrow() }
      val b = async { effectB().getOrThrow() }
      val c = async { effectC().getOrThrow() }
      val d = async { effectD().getOrThrow() }
      val e = async { effectE().getOrThrow() }
      val f = async { effectF().getOrThrow() }
      val g = async { effectG().getOrThrow() }
      val h = async { effectH().getOrThrow() }
      val i = async { effectI().getOrThrow() }
      val j = async { effectJ().getOrThrow() }
      val k = async { effectK().getOrThrow() }
      val l = async { effectL().getOrThrow() }
      val m = async { effectM().getOrThrow() }
      val n = async { effectN().getOrThrow() }
      val o = async { effectO().getOrThrow() }
      val p = async { effectP().getOrThrow() }
      val q = async { effectQ().getOrThrow() }
      val r = async { effectR().getOrThrow() }
      val s = async { effectS().getOrThrow() }
      awaitAll(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
      Tuple19(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await(), i.await(), j.await(), k.await(), l.await(), m.await(), n.await(), o.await(), p.await(), q.await(), r.await(), s.await())
   }
}

/**
 * Executes the given effects in parallel, failing fast.
 */
suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> parN(
   effectA: suspend () -> Result<A>,
   effectB: suspend () -> Result<B>,
   effectC: suspend () -> Result<C>,
   effectD: suspend () -> Result<D>,
   effectE: suspend () -> Result<E>,
   effectF: suspend () -> Result<F>,
   effectG: suspend () -> Result<G>,
   effectH: suspend () -> Result<H>,
   effectI: suspend () -> Result<I>,
   effectJ: suspend () -> Result<J>,
   effectK: suspend () -> Result<K>,
   effectL: suspend () -> Result<L>,
   effectM: suspend () -> Result<M>,
   effectN: suspend () -> Result<N>,
   effectO: suspend () -> Result<O>,
   effectP: suspend () -> Result<P>,
   effectQ: suspend () -> Result<Q>,
   effectR: suspend () -> Result<R>,
   effectS: suspend () -> Result<S>,
   effectT: suspend () -> Result<T>,
): Result<Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>> = runCatching {
   coroutineScope {
      val a = async { effectA().getOrThrow() }
      val b = async { effectB().getOrThrow() }
      val c = async { effectC().getOrThrow() }
      val d = async { effectD().getOrThrow() }
      val e = async { effectE().getOrThrow() }
      val f = async { effectF().getOrThrow() }
      val g = async { effectG().getOrThrow() }
      val h = async { effectH().getOrThrow() }
      val i = async { effectI().getOrThrow() }
      val j = async { effectJ().getOrThrow() }
      val k = async { effectK().getOrThrow() }
      val l = async { effectL().getOrThrow() }
      val m = async { effectM().getOrThrow() }
      val n = async { effectN().getOrThrow() }
      val o = async { effectO().getOrThrow() }
      val p = async { effectP().getOrThrow() }
      val q = async { effectQ().getOrThrow() }
      val r = async { effectR().getOrThrow() }
      val s = async { effectS().getOrThrow() }
      val t = async { effectT().getOrThrow() }
      awaitAll(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
      Tuple20(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await(), i.await(), j.await(), k.await(), l.await(), m.await(), n.await(), o.await(), p.await(), q.await(), r.await(), s.await(), t.await())
   }
}
