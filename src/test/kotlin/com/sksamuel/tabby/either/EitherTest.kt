package com.sksamuel.tabby.either

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class EitherTest : FunSpec() {
   init {

      test("cond(true, ifFalse, ifTrue) returns Right(ifTrue())") {
         Either.cond(true, { "L" }, { "R" }) shouldBe Either.Right("R")
      }

      test("cond(false, ifFalse, ifTrue) returns Left(ifFalse())") {
         Either.cond(false, { "L" }, { "R" }) shouldBe Either.Left("L")
      }

      test("cond does not invoke the unused side") {
         var leftCalls = 0
         var rightCalls = 0
         Either.cond(true, { leftCalls++; "L" }, { rightCalls++; "R" })
         leftCalls shouldBe 0
         rightCalls shouldBe 1
      }

      test("cond with lazy test only calls test once") {
         var calls = 0
         Either.cond({ calls++; true }, { "L" }, { "R" })
         calls shouldBe 1
      }

      test("swap turns Left(a) into Right(a)") {
         val left: Either<String, Int> = "x".left()
         left.swap() shouldBe Either.Right("x")
      }

      test("swap turns Right(b) into Left(b)") {
         val right: Either<Int, String> = "x".right()
         right.swap() shouldBe Either.Left("x")
      }

      test("getLeftOrNull returns the left value or null") {
         val left: Either<String, Int> = "L".left()
         left.getLeftOrNull() shouldBe "L"

         val right: Either<String, Int> = 1.right()
         right.getLeftOrNull().shouldBeNull()
      }

      test("getRightOrNull returns the right value or null") {
         val right: Either<String, Int> = 1.right()
         right.getRightOrNull() shouldBe 1

         val left: Either<String, Int> = "L".left()
         left.getRightOrNull().shouldBeNull()
      }

      test("bimap applies ifLeft to Left") {
         val left: Either<String, Int> = "x".left()
         left.bimap({ "$it!" }, { it + 1 }) shouldBe Either.Left("x!")
      }

      test("bimap applies ifRight to Right") {
         val right: Either<String, Int> = 1.right()
         right.bimap({ "$it!" }, { it + 1 }) shouldBe Either.Right(2)
      }

      test("mapLeft maps the Left side only") {
         val left: Either<String, Int> = "x".left()
         left.mapLeft { "$it!" } shouldBe Either.Left("x!")

         val right: Either<String, Int> = 1.right()
         right.mapLeft { "$it!" } shouldBe Either.Right(1)
      }

      test("map maps the Right side only") {
         val right: Either<String, Int> = 1.right()
         right.map { it + 1 } shouldBe Either.Right(2)

         val left: Either<String, Int> = "x".left()
         left.map { it + 1 } shouldBe Either.Left("x")
      }

      test("flatMap on Right invokes f, on Left returns this") {
         val right: Either<String, Int> = 1.right()
         right.flatMap { (it + 1).right() } shouldBe Either.Right(2)

         val left: Either<String, Int> = "L".left()
         left.flatMap { (it + 1).right() } shouldBe Either.Left("L")
      }

      test("flatMapLeft on Left invokes f, on Right returns this") {
         val left: Either<String, Int> = "L".left()
         left.flatMapLeft { "$it!".left() } shouldBe Either.Left("L!")

         val right: Either<String, Int> = 1.right()
         right.flatMapLeft { "$it!".left() } shouldBe Either.Right(1)
      }

      test("flatten unwraps an Either<A, Either<A, B>>") {
         val nestedRightRight: Either<String, Either<String, Int>> = Either.Right(5.right())
         nestedRightRight.flatten() shouldBe Either.Right(5)

         val nestedLeft: Either<String, Either<String, Int>> = "outer".left()
         nestedLeft.flatten() shouldBe Either.Left("outer")

         val nestedInnerLeft: Either<String, Either<String, Int>> = Either.Right("inner".left())
         nestedInnerLeft.flatten() shouldBe Either.Left("inner")
      }

      test("recover replaces Left with Right(ifLeft(a))") {
         val left: Either<String, String> = "x".left()
         left.recover { "$it!" } shouldBe Either.Right("x!")
      }

      test("recover leaves Right as is") {
         val right: Either<String, String> = "y".right()
         right.recover { "L" } shouldBe Either.Right("y")
      }

      test("recoverWith on Left invokes ifLeft to produce a new Either") {
         val left: Either<String, String> = "x".left()
         left.recoverWith { "$it!".right() } shouldBe Either.Right("x!")
         left.recoverWith { "$it!".left() } shouldBe Either.Left("x!")
      }

      test("recoverWith leaves Right as is") {
         val right: Either<String, String> = "y".right()
         right.recoverWith { "L".left() } shouldBe Either.Right("y")
      }

      test("orElse on Left returns the alternative") {
         val left: Either<String, String> = "x".left()
         left.orElse { "y".right() } shouldBe Either.Right("y")
      }

      test("orElse on Right ignores the alternative") {
         val right: Either<String, String> = "y".right()
         right.orElse { "L".left() } shouldBe Either.Right("y")
      }

      test("either { ... } catches throwables into Left") {
         val ex = RuntimeException("oops")
         either<Int> { throw ex } shouldBe Either.Left(ex)
      }

      test("either { ... } returns Right on success") {
         either { 5 } shouldBe Either.Right(5)
      }

      test("leftIfNull turns null into Left, non-null into Right") {
         val nullStr: String? = null
         nullStr.leftIfNull { "L" } shouldBe Either.Left("L")
         val nonNull: String? = "x"
         nonNull.leftIfNull { "L" } shouldBe Either.Right("x")
      }

      test("rightIfNotNull turns non-null into Right, null into Left") {
         val nonNull: String? = "x"
         nonNull.rightIfNotNull { "L" } shouldBe Either.Right("x")
         val nullStr: String? = null
         nullStr.rightIfNotNull { "L" } shouldBe Either.Left("L")
      }

      test("leftIf turns value into Left when predicate is true") {
         "x".leftIf("L") { it == "x" } shouldBe Either.Left("L")
         "x".leftIf("L") { it == "y" } shouldBe Either.Right("x")
      }

      test("rightIf turns value into Right when predicate is true") {
         "x".rightIf("L") { it == "x" } shouldBe Either.Right("x")
         "x".rightIf("L") { it == "y" } shouldBe Either.Left("L")
      }

      test("split partitions a list of Either into lefts and rights") {
         val list: List<Either<String, Int>> = listOf("a".left(), 1.right(), "b".left(), 2.right())
         val (lefts, rights) = list.split()
         lefts shouldBe listOf("a", "b")
         rights shouldBe listOf(1, 2)
      }

      test("zip pairs Right values, fails on first Left") {
         val r1: Either<String, Int> = 1.right()
         val r2: Either<String, Int> = 2.right()
         r1.zip(r2) shouldBe Either.Right(1 to 2)

         val l: Either<String, Int> = "L".left()
         l.zip(r2) shouldBe Either.Left("L")
         r1.zip(l) shouldBe Either.Left("L")
      }

      test("toResult on Either<Throwable, B> maps Left → failure, Right → success") {
         val ex = RuntimeException()
         val left: Either<Throwable, Int> = ex.left()
         left.toResult().exceptionOrNull() shouldBe ex

         val right: Either<Throwable, Int> = 5.right()
         right.toResult().getOrThrow() shouldBe 5
      }

      test("toResult(f) maps Left through f and into a Throwable failure") {
         val left: Either<String, Int> = "boom".left()
         left.toResult { RuntimeException(it) }.exceptionOrNull()?.message shouldBe "boom"

         val right: Either<String, Int> = 5.right()
         right.toResult { RuntimeException(it) }.getOrThrow() shouldBe 5
      }
   }
}
