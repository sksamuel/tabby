package com.sksamuel.tabby.io

import com.sksamuel.tabby.Option
import com.sksamuel.tabby.none
import com.sksamuel.tabby.some
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
interface Schedule<in T> {

   /**
    * Returns a function, which is used to determine when, or if, to re-run the effect.
    * If none is returned, then the scheduling will conclude.
    * The function will be passed the output of the previous execution.
    */
   suspend fun schedule(): (T) -> Option<Duration>

   /**
    * Execute once after the initial run.
    */
   object Once : Schedule<Any> {
      override suspend fun schedule(): (Any) -> Option<Duration> {
         var ran = false
         return {
            if (ran) none else {
               ran = true
               0.milliseconds.some()
            }
         }
      }
   }

   /**
    * Executes forever, until the first error.
    */
   object Forever : Schedule<Any> {
      override suspend fun schedule(): (Any) -> Option<Duration> = { 0.milliseconds.some() }
   }

   class While<T>(private val predicate: (T) -> Boolean) : Schedule<T> {
      override suspend fun schedule(): (T) -> Option<Duration> = {
         if (predicate(it)) 0.milliseconds.some() else none
      }
   }
}
