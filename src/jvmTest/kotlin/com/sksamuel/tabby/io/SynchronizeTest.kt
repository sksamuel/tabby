package com.sksamuel.tabby.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import java.util.concurrent.atomic.AtomicReference

class SynchronizeTest : FunSpec() {
   init {
      test("synchronize should isolate depending on semaphore permits") {
         val ref = AtomicReference("")
         val mutex = Semaphore(1)
         val a = IO.effect {
            repeat(10) {
               ref.updateAndGet { it + "a" }
               delay(25)
            }
         }.synchronize(mutex)
         val b = IO.effect {
            repeat(10) {
               ref.updateAndGet { it + "b" }
               delay(25)
            }
         }.synchronize(mutex)

         coroutineScope {
            launch { a.run() }
            launch { b.run() }
         }

         ref.get() shouldBe "aaaaaaaaaabbbbbbbbbb"
      }
   }
}
