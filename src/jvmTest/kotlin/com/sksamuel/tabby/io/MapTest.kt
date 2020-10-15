package com.sksamuel.tabby.io

import com.sksamuel.tabby.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MapTest : FunSpec() {
   init {
      test("map should invoke combinator on success") {
         IO.effect { "foo" }.map { it.length }.run() shouldBe 3.right()
         IO.effect { "foo" }.map { it.length }.map { it + 3 }.run() shouldBe 6.right()
      }
   }
}
