package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.success
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MapTest : FunSpec() {
   init {
      test("map should invoke combinator on success") {
         IO { "foo" }.map { it.length }.run() shouldBe 3.success()
         IO { "foo" }.map { it.length }.map { it + 3 }.run() shouldBe 6.success()
      }
   }
}
