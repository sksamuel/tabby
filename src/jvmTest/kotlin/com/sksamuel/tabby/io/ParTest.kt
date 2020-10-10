package com.sksamuel.tabby.io

import com.sksamuel.tabby.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
class ParTest : FunSpec() {
   init {
      test("multiple ios should run in parallel").config(timeout = 1500.milliseconds) {
         val a = IO.effect {
            delay(1000)
            "foo"
         }
         val b = IO.effect {
            delay(1000)
            "bar"
         }
         IO.par(a, b).run() shouldBe listOf("foo", "bar").right()
      }
   }
}
