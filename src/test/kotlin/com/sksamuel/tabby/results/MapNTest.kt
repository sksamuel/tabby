package com.sksamuel.tabby.results

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MapNTest : FunSpec() {
   init {

      test("mapN2 should return success when all inputs succeed") {
         Result.mapN(Result.success(1), Result.success(2)) { a, b -> a + b }
            .getOrThrow() shouldBe 3
      }

      test("mapN3 should return success when all inputs succeed") {
         Result.mapN(Result.success(1), Result.success(2), Result.success(3)) { a, b, c -> a + b + c }
            .getOrThrow() shouldBe 6
      }

      test("mapN4 should return success when all inputs succeed") {
         Result.mapN(
            Result.success(1),
            Result.success(2),
            Result.success(3),
            Result.success(4),
         ) { a, b, c, d -> a + b + c + d }.getOrThrow() shouldBe 10
      }

      test("mapN5 should return success when all inputs succeed") {
         Result.mapN(
            Result.success(1),
            Result.success(2),
            Result.success(3),
            Result.success(4),
            Result.success(5),
         ) { a, b, c, d, e -> a + b + c + d + e }.getOrThrow() shouldBe 15
      }

      test("mapN6 should return success when all inputs succeed") {
         Result.mapN(
            Result.success(1),
            Result.success(2),
            Result.success(3),
            Result.success(4),
            Result.success(5),
            Result.success(6),
         ) { a, b, c, d, e, f -> a + b + c + d + e + f }.getOrThrow() shouldBe 21
      }

      test("mapN4 should propagate the failing argument's exception") {
         val expected = RuntimeException("d failed")
         val actual = Result.mapN(
            Result.success(1),
            Result.success(2),
            Result.success(3),
            Result.failure<Int>(expected),
         ) { a, b, c, d -> a + b + c + d }.exceptionOrNull()
         actual shouldBe expected
      }

      test("mapN5 should propagate the failing argument's exception (d)") {
         val expected = RuntimeException("d failed")
         val actual = Result.mapN(
            Result.success(1),
            Result.success(2),
            Result.success(3),
            Result.failure<Int>(expected),
            Result.success(5),
         ) { a, b, c, d, e -> a + b + c + d + e }.exceptionOrNull()
         actual shouldBe expected
      }

      test("mapN5 should propagate the failing argument's exception (e)") {
         val expected = RuntimeException("e failed")
         val actual = Result.mapN(
            Result.success(1),
            Result.success(2),
            Result.success(3),
            Result.success(4),
            Result.failure<Int>(expected),
         ) { a, b, c, d, e -> a + b + c + d + e }.exceptionOrNull()
         actual shouldBe expected
      }

      test("mapN6 should propagate the failing argument's exception (d)") {
         val expected = RuntimeException("d failed")
         val actual = Result.mapN(
            Result.success(1),
            Result.success(2),
            Result.success(3),
            Result.failure<Int>(expected),
            Result.success(5),
            Result.success(6),
         ) { a, b, c, d, e, f -> a + b + c + d + e + f }.exceptionOrNull()
         actual shouldBe expected
      }

      test("mapN6 should propagate the failing argument's exception (e)") {
         val expected = RuntimeException("e failed")
         val actual = Result.mapN(
            Result.success(1),
            Result.success(2),
            Result.success(3),
            Result.success(4),
            Result.failure<Int>(expected),
            Result.success(6),
         ) { a, b, c, d, e, f -> a + b + c + d + e + f }.exceptionOrNull()
         actual shouldBe expected
      }

      test("mapN6 should propagate the failing argument's exception (f)") {
         val expected = RuntimeException("f failed")
         val actual = Result.mapN(
            Result.success(1),
            Result.success(2),
            Result.success(3),
            Result.success(4),
            Result.success(5),
            Result.failure<Int>(expected),
         ) { a, b, c, d, e, f -> a + b + c + d + e + f }.exceptionOrNull()
         actual shouldBe expected
      }
   }
}
