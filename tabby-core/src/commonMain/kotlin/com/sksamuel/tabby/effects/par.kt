package com.sksamuel.tabby.effects

import com.sksamuel.tabby.Tuple10
import com.sksamuel.tabby.Tuple2
import com.sksamuel.tabby.Tuple3
import com.sksamuel.tabby.Tuple4
import com.sksamuel.tabby.Tuple5
import com.sksamuel.tabby.Tuple6
import com.sksamuel.tabby.Tuple7
import com.sksamuel.tabby.Tuple8
import com.sksamuel.tabby.Tuple9
import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.catch
import com.sksamuel.tabby.`try`.getValueOrElse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

fun <A> List<IO<A>>.par(): IO<List<A>> = IO.par(this)

fun <A> IO.Companion.par(effects: List<IO<A>>): IO<List<A>> = object : IO<List<A>>() {
   override suspend fun apply(): Try<List<A>> = catch {
      coroutineScope {
         val ds = effects.map {
            async {
               it.run().getValueOrElse { throw it }
            }
         }
         ds.awaitAll()
      }
   }
}

fun <A> IO.Companion.par(vararg effects: IO<A>): IO<List<A>> = IO.par(effects.asList())

fun <A, B> IO.Companion.parN(ioa: IO<A>, iob: IO<B>) = object : IO<Tuple2<A, B>>() {
   override suspend fun apply(): Try<Tuple2<A, B>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getValueOrElse { throw it }
         }
         val b = async {
            iob.run().getValueOrElse { throw it }
         }
         Tuple2(a.await(), b.await())
      }
   }
}

fun <A, B, C> IO.Companion.parN(ioa: IO<A>, iob: IO<B>, ioc: IO<C>) = object : IO<Tuple3<A, B, C>>() {
   override suspend fun apply(): Try<Tuple3<A, B, C>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getValueOrElse { throw it }
         }
         val b = async {
            iob.run().getValueOrElse { throw it }
         }
         val c = async {
            ioc.run().getValueOrElse { throw it }
         }
         Tuple3(a.await(), b.await(), c.await())
      }
   }
}

fun <A, B, C, D> IO.Companion.parN(
   ioa: IO<A>,
   iob: IO<B>,
   ioc: IO<C>,
   iod: IO<D>
) = object : IO<Tuple4<A, B, C, D>>() {
   override suspend fun apply(): Try<Tuple4<A, B, C, D>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getValueOrElse { throw it }
         }
         val b = async {
            iob.run().getValueOrElse { throw it }
         }
         val c = async {
            ioc.run().getValueOrElse { throw it }
         }
         val d = async {
            iod.run().getValueOrElse { throw it }
         }
         Tuple4(a.await(), b.await(), c.await(), d.await())
      }
   }
}

fun <A, B, C, D, E> IO.Companion.parN(
   ioa: IO<A>,
   iob: IO<B>,
   ioc: IO<C>,
   iod: IO<D>,
   ioe: IO<E>,
) = object : IO<Tuple5<A, B, C, D, E>>() {
   override suspend fun apply(): Try<Tuple5<A, B, C, D, E>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getValueOrElse { throw it }
         }
         val b = async {
            iob.run().getValueOrElse { throw it }
         }
         val c = async {
            ioc.run().getValueOrElse { throw it }
         }
         val d = async {
            iod.run().getValueOrElse { throw it }
         }
         val e = async {
            ioe.run().getValueOrElse { throw it }
         }
         Tuple5(a.await(), b.await(), c.await(), d.await(), e.await())
      }
   }
}

fun <A, B, C, D, E, F> IO.Companion.parN(
   ioa: IO<A>,
   iob: IO<B>,
   ioc: IO<C>,
   iod: IO<D>,
   ioe: IO<E>,
   iof: IO<F>,
) = object : IO<Tuple6<A, B, C, D, E, F>>() {
   override suspend fun apply(): Try<Tuple6<A, B, C, D, E, F>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getValueOrElse { throw it }
         }
         val b = async {
            iob.run().getValueOrElse { throw it }
         }
         val c = async {
            ioc.run().getValueOrElse { throw it }
         }
         val d = async {
            iod.run().getValueOrElse { throw it }
         }
         val e = async {
            ioe.run().getValueOrElse { throw it }
         }
         val f = async {
            iof.run().getValueOrElse { throw it }
         }
         Tuple6(a.await(), b.await(), c.await(), d.await(), e.await(), f.await())
      }
   }
}

fun <A, B, C, D, E, F, G> IO.Companion.parN(
   ioa: IO<A>,
   iob: IO<B>,
   ioc: IO<C>,
   iod: IO<D>,
   ioe: IO<E>,
   iof: IO<F>,
   iog: IO<G>,
) = object : IO<Tuple7<A, B, C, D, E, F, G>>() {
   override suspend fun apply(): Try<Tuple7<A, B, C, D, E, F, G>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getValueOrElse { throw it }
         }
         val b = async {
            iob.run().getValueOrElse { throw it }
         }
         val c = async {
            ioc.run().getValueOrElse { throw it }
         }
         val d = async {
            iod.run().getValueOrElse { throw it }
         }
         val e = async {
            ioe.run().getValueOrElse { throw it }
         }
         val f = async {
            iof.run().getValueOrElse { throw it }
         }
         val g = async {
            iog.run().getValueOrElse { throw it }
         }
         Tuple7(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await())
      }
   }
}

fun <A, B, C, D, E, F, G, H> IO.Companion.parN(
   ioa: IO<A>,
   iob: IO<B>,
   ioc: IO<C>,
   iod: IO<D>,
   ioe: IO<E>,
   iof: IO<F>,
   iog: IO<G>,
   ioh: IO<H>,
) = object : IO<Tuple8<A, B, C, D, E, F, G, H>>() {
   override suspend fun apply(): Try<Tuple8<A, B, C, D, E, F, G, H>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getValueOrElse { throw it }
         }
         val b = async {
            iob.run().getValueOrElse { throw it }
         }
         val c = async {
            ioc.run().getValueOrElse { throw it }
         }
         val d = async {
            iod.run().getValueOrElse { throw it }
         }
         val e = async {
            ioe.run().getValueOrElse { throw it }
         }
         val f = async {
            iof.run().getValueOrElse { throw it }
         }
         val g = async {
            iog.run().getValueOrElse { throw it }
         }
         val h = async {
            ioh.run().getValueOrElse { throw it }
         }
         Tuple8(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await())
      }
   }
}

fun <A, B, C, D, E, F, G, H, I> IO.Companion.parN(
   ioa: IO<A>,
   iob: IO<B>,
   ioc: IO<C>,
   iod: IO<D>,
   ioe: IO<E>,
   iof: IO<F>,
   iog: IO<G>,
   ioh: IO<H>,
   ioi: IO<I>,
) = object : IO<Tuple9<A, B, C, D, E, F, G, H, I>>() {
   override suspend fun apply(): Try<Tuple9<A, B, C, D, E, F, G, H, I>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getValueOrElse { throw it }
         }
         val b = async {
            iob.run().getValueOrElse { throw it }
         }
         val c = async {
            ioc.run().getValueOrElse { throw it }
         }
         val d = async {
            iod.run().getValueOrElse { throw it }
         }
         val e = async {
            ioe.run().getValueOrElse { throw it }
         }
         val f = async {
            iof.run().getValueOrElse { throw it }
         }
         val g = async {
            iog.run().getValueOrElse { throw it }
         }
         val h = async {
            ioh.run().getValueOrElse { throw it }
         }
         val i = async {
            ioi.run().getValueOrElse { throw it }
         }
         Tuple9(a.await(), b.await(), c.await(), d.await(), e.await(), f.await(), g.await(), h.await(), i.await())
      }
   }
}

fun <A, B, C, D, E, F, G, H, I, J> IO.Companion.parN(
   ioa: IO<A>,
   iob: IO<B>,
   ioc: IO<C>,
   iod: IO<D>,
   ioe: IO<E>,
   iof: IO<F>,
   iog: IO<G>,
   ioh: IO<H>,
   ioi: IO<I>,
   ioj: IO<J>,
) = object : IO<Tuple10<A, B, C, D, E, F, G, H, I, J>>() {
   override suspend fun apply(): Try<Tuple10<A, B, C, D, E, F, G, H, I, J>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getValueOrElse { throw it }
         }
         val b = async {
            iob.run().getValueOrElse { throw it }
         }
         val c = async {
            ioc.run().getValueOrElse { throw it }
         }
         val d = async {
            iod.run().getValueOrElse { throw it }
         }
         val e = async {
            ioe.run().getValueOrElse { throw it }
         }
         val f = async {
            iof.run().getValueOrElse { throw it }
         }
         val g = async {
            iog.run().getValueOrElse { throw it }
         }
         val h = async {
            ioh.run().getValueOrElse { throw it }
         }
         val i = async {
            ioi.run().getValueOrElse { throw it }
         }
         val j = async {
            ioj.run().getValueOrElse { throw it }
         }
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
}
