package com.sksamuel.tabby.effects

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class RepeatWhileTest : FunSpec() {
   init {

      test("IO.repeatWhile(schedule, predicate) should contine until predicate is false") {
         var counter = 0
         IO { counter++ }.repeatWhile(Schedule.forever) { counter < 10 }.run()
         counter shouldBe 10
      }

      test("IO.repeatWhile(schedule, predicate) should contine until schedule expires") {
         val counter = AtomicInteger(0)
         IO { counter.incrementAndGet() }.repeatWhile(Schedule.iterations(5)) { counter.get() < 10 }.run()
         counter.get() shouldBe 6
      }

      test("IO.repeatWhile(f) should contine while function returns a predicate") {
         val counter = AtomicInteger(0)
         IO { counter.incrementAndGet() }.repeatWhile { if (it.index < 10) Schedule.once else Schedule.never }.run()
         counter.get() shouldBe 11
      }
   }
}
