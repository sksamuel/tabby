package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe

class FlatMapTest  : FunSpec() {
   init {
      test("flatMapIf with default val") {
         val limit = 10
         fun isMoreThanLimit(value: Int): Boolean = value > limit

         val isMapped = "mapped"
         val isNotMapped = "not-mapped"

         for (i in limit - 5..limit + 5) {
            val originalRes = Result.success(i)
            val mappedRes = originalRes.flatMapIf(::isMoreThanLimit, { Result.success(isMapped) }, isNotMapped)

            mappedRes.shouldBeSuccess()
            mappedRes.getOrThrow() shouldBe if (isMoreThanLimit(i)) isMapped else isNotMapped
         }
      }

      test("flatMapIf with branched mapping") {
         val limit = 10
         fun isMoreThanLimit(value: Int): Boolean = value > limit

         val isMapped = "mapped"
         val isNotMapped = "not-mapped"

         for (i in limit - 5..limit + 5) {
            val originalRes = Result.success(i)
            val mappedRes =
               originalRes.flatMapIf(::isMoreThanLimit, { Result.success(isMapped) }, { Result.success(isNotMapped) })

            mappedRes.shouldBeSuccess()
            mappedRes.getOrThrow() shouldBe if (isMoreThanLimit(i)) isMapped else isNotMapped
         }
      }
   }
}
