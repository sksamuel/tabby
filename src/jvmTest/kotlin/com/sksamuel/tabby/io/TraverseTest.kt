package com.sksamuel.tabby.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class TraverseTest : FunSpec() {
   init {

      test("traverse should collect all successes") {
         IO.traverse(
            IO.effect { "foo" },
            IO.effect { "bar" },
            IO.effect { "baz" }
         ).run().getRightUnsafe() shouldBe listOf("foo", "bar", "baz")
      }

      test("traverse should should circuit on failure") {
         val counter = AtomicInteger(0)
         IO.traverse(
            IO.effect { counter.incrementAndGet() },
            IO.effect { error("boom") },
            IO.effect { counter.incrementAndGet() }
         ).run().getLeftUnsafe().message shouldBe "boom"
         counter.get().shouldBe(1)
      }
   }
}
