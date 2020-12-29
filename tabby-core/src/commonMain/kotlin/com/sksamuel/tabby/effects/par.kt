package com.sksamuel.tabby.effects

import com.sksamuel.tabby.Tuple2
import com.sksamuel.tabby.Tuple3
import com.sksamuel.tabby.Tuple4
import com.sksamuel.tabby.Tuple5
import com.sksamuel.tabby.either.Try
import com.sksamuel.tabby.either.catch
import com.sksamuel.tabby.either.getRightOrElse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

fun <A> IO.Companion.par(effects: List<IO<A>>): IO<List<A>> = object : IO<List<A>>() {
   override suspend fun apply(): Try<List<A>> = catch {
      coroutineScope {
         val ds = effects.map {
            async {
               it.run().getRightOrElse { throw it }
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
            ioa.run().getRightOrElse { throw it }
         }
         val b = async {
            iob.run().getRightOrElse { throw it }
         }
         Tuple2(a.await(), b.await())
      }
   }
}

fun <A, B, C> IO.Companion.parN(ioa: IO<A>, iob: IO<B>, ioc: IO<C>) = object : IO<Tuple3<A, B, C>>() {
   override suspend fun apply(): Try<Tuple3<A, B, C>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getRightOrElse { throw it }
         }
         val b = async {
            iob.run().getRightOrElse { throw it }
         }
         val c = async {
            ioc.run().getRightOrElse { throw it }
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
            ioa.run().getRightOrElse { throw it }
         }
         val b = async {
            iob.run().getRightOrElse { throw it }
         }
         val c = async {
            ioc.run().getRightOrElse { throw it }
         }
         val d = async {
            iod.run().getRightOrElse { throw it }
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
   ioe: IO<E>
) = object : IO<Tuple5<A, B, C, D, E>>() {
   override suspend fun apply(): Try<Tuple5<A, B, C, D, E>> = catch {
      coroutineScope {
         val a = async {
            ioa.run().getRightOrElse { throw it }
         }
         val b = async {
            iob.run().getRightOrElse { throw it }
         }
         val c = async {
            ioc.run().getRightOrElse { throw it }
         }
         val d = async {
            iod.run().getRightOrElse { throw it }
         }
         val e = async {
            ioe.run().getRightOrElse { throw it }
         }
         Tuple5(a.await(), b.await(), c.await(), d.await(), e.await())
      }
   }
}
