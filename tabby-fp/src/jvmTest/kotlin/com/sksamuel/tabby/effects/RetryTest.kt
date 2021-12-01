package com.sksamuel.tabby.effects

import com.sksamuel.tabby.results.exceptionOrThrow
import com.sksamuel.tabby.results.failure
import com.sksamuel.tabby.results.success
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeBetween
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class RetryTest : FunSpec() {
   init {

      test("retry should invoke up to Schedule.iterations") {
         var iterations = 0
         retry(Schedule.iterations(5)) {
            iterations++
            "boom".failure()
         }.exceptionOrThrow().message shouldBe "boom"
         iterations shouldBe 6
      }

      test("retry should return once successful") {
         var iterations = 0
         retry(Schedule.forever) {
            iterations++
            if (iterations == 5) "foo".success() else "boom".failure()
         }.getOrThrow() shouldBe "foo"
         iterations shouldBe 5
      }

      test("retry with delayed schedule") {
         measureTime {
            retry(Schedule.iterations(3).delay(100.milliseconds)) { "boom".failure() }
         }.inWholeMilliseconds.shouldBeBetween(300, 400)
      }
   }
}
