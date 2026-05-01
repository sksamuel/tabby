package com.sksamuel.tabby.either

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EitherSequenceTest : FunSpec() {
   init {

      test("sequence returns all rights when there are no lefts") {
         val input: List<Either<String, Int>> = listOf(1.right(), 2.right(), 3.right())
         input.sequence() shouldBe listOf(1, 2, 3).right()
      }

      test("sequence returns lefts when any element is a left") {
         val input: List<Either<String, Int>> = listOf(1.right(), "boom".left(), 3.right(), "bang".left())
         input.sequence() shouldBe listOf("boom", "bang").left()
      }

      test("sequence returns an empty right for an empty input") {
         val input: List<Either<String, Int>> = emptyList()
         input.sequence() shouldBe emptyList<Int>().right()
      }

      test("sequence returns all lefts when every element is a left") {
         val input: List<Either<String, Int>> = listOf("a".left(), "b".left())
         input.sequence() shouldBe listOf("a", "b").left()
      }
   }
}
