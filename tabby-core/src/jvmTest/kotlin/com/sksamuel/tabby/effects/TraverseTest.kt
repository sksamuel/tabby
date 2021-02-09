package com.sksamuel.tabby.effects

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class TraverseTest : FunSpec() {
   init {

      test("traverse should collect all successes") {
         IO.traverse(
            IO { "foo" },
            IO { "bar" },
            IO { "baz" }
         ).run().getValueUnsafe() shouldBe listOf("foo", "bar", "baz")
      }

      test("traverse should should circuit on failure") {
         val counter = AtomicInteger(0)
         IO.traverse(
            IO { counter.incrementAndGet() },
            IO { error("boom") },
            IO { counter.incrementAndGet() }
         ).run().getErrorUnsafe().message shouldBe "boom"
         counter.get().shouldBe(1)
      }
   }
}
