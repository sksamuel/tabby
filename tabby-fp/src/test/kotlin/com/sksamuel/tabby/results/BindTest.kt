package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BindTest : FunSpec() {
   init {
      test("runCatching with bind") {
         val a = "a".success()
         val b = "b".success()
         catching {
            a.bind() + b.bind()
         } shouldBe "ab".success()
      }
   }
}
