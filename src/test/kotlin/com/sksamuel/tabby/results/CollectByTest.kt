package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CollectByTest : FunSpec() {
   init {

      test("collectBy returns f's mapped values on the success side") {
         val (errors, values) = listOf(1, 2, 3).collectBy { (it * 10).success() }
         errors shouldBe emptyList()
         values shouldBe listOf(10, 20, 30)
      }

      test("collectBy splits errors and mapped values") {
         val boom = RuntimeException("boom")
         val (errors, values) = listOf(1, 2, 3).collectBy {
            if (it == 2) Result.failure(boom) else (it * 10).success()
         }
         errors shouldBe listOf(boom)
         values shouldBe listOf(10, 30)
      }

      test("collectBy returns all errors when every element fails") {
         val (errors, values) = listOf("a", "b").collectBy<String, Int> {
            Result.failure(RuntimeException(it))
         }
         errors.map { it.message } shouldBe listOf("a", "b")
         values shouldBe emptyList()
      }

      test("collectBy invokes f exactly once per element") {
         var calls = 0
         listOf(1, 2, 3, 4).collectBy {
            calls++
            if (it % 2 == 0) Result.failure(RuntimeException()) else it.success()
         }
         calls shouldBe 4
      }
   }
}
