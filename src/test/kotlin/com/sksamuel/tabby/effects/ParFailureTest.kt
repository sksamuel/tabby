package com.sksamuel.tabby.effects

import com.sksamuel.tabby.results.success
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import kotlinx.coroutines.delay

class ParFailureTest : FunSpec() {
   init {
      test("a parN component failure should cancel all other component effects") {
         val result: Result<Triple<Int, String, List<Int>>> = parN(
            { delay(10000); 1.success() }, // will be preempted
            { delay(10); Result.failure(RuntimeException("boom")) },
            { delay(10000); listOf(1, 2, 3).success() }, // will be preempted
         )
         result.shouldBeFailure()
      }
   }
}
