package com.sksamuel.tabby.io

import com.sksamuel.tabby.none
import com.sksamuel.tabby.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean

class RetryTest : FunSpec() {
   init {

      test("IO.retry should invoke up to Schedule.iterations") {
         var iterations = 0
         IO.effect {
            iterations++
            error("boom")
         }.retry(Schedule.iterations(5)).run().getLeftUnsafe().message shouldBe "boom"
         iterations shouldBe 6
      }

      test("IO.retry should return once successful") {
         var iterations = 0
         IO.effect {
            iterations++
            if (iterations == 5) "foo" else error("boom")
         }.retry(Schedule.forever()).run() shouldBe "foo".right()
         iterations shouldBe 5
      }

      test("IO.retry should not use schedule if successful") {
         val triggered = AtomicBoolean(false)
         IO.effect { "foo" }.retry {
            triggered.set(true)
            Decision.Continue(none, Schedule.forever())
         }.run()
         triggered.get() shouldBe false
      }
   }
}
