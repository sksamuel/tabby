package com.sksamuel.tabby.`try`

import com.sksamuel.tabby.effects.IO
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FlatMapTest : FunSpec() {
   init {
      test("flatMap with try") {
         Try.success("a").flatMap { Try.success("b") } shouldBe Try.success("b")
      }
      test("flatMap with IO") {
         Try.success("a").flatMap { IO.pure("b") }.runUnsafe() shouldBe "b"
      }
   }
}
