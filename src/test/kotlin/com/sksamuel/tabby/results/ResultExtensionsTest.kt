package com.sksamuel.tabby.results

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe

class ResultExtensionsTest : FunSpec() {
   init {

      test("then runs f for its side effect; result value is the original on f-success") {
         var fCalled = 0
         Result.success("orig").then { fCalled++; Result.success(99) }.getOrThrow() shouldBe "orig"
         fCalled shouldBe 1
      }

      test("then returns f's failure if f fails") {
         val ex = RuntimeException("from f")
         Result.success("orig").then { Result.failure<Int>(ex) }.exceptionOrNull() shouldBe ex
      }

      test("then short-circuits on receiver failure (does not invoke f)") {
         var fCalled = 0
         val ex = RuntimeException("orig")
         Result.failure<String>(ex).then { fCalled++; Result.success(0) }.exceptionOrNull() shouldBe ex
         fCalled shouldBe 0
      }

      test("flatten unwraps Result<Result<A>>") {
         Result.success(Result.success(5)).flatten().getOrThrow() shouldBe 5

         val ex = RuntimeException()
         Result.success(Result.failure<Int>(ex)).flatten().exceptionOrNull() shouldBe ex
         Result.failure<Result<Int>>(ex).flatten().exceptionOrNull() shouldBe ex
      }

      test("omit converts a successful Result<A> into Result<Unit>") {
         Result.success("x").omit() shouldBe Result.success(Unit)
         val ex = RuntimeException()
         Result.failure<String>(ex).omit().exceptionOrNull() shouldBe ex
      }

      test("exceptionOrThrow returns failure exception, throws ISE on success") {
         val ex = RuntimeException("boom")
         Result.failure<Int>(ex).exceptionOrThrow() shouldBe ex
         shouldThrow<IllegalStateException> { Result.success(1).exceptionOrThrow() }
      }

      test("recover(f) on success returns this; on failure invokes f") {
         Result.success(1).recover { Result.success(99) }.getOrThrow() shouldBe 1
         Result.failure<Int>(RuntimeException()).recover { Result.success(99) }.getOrThrow() shouldBe 99
      }

      test("recoverIf invokes f only when predicate matches the throwable") {
         val match = RuntimeException("match")
         val nomatch = RuntimeException("nomatch")
         Result.failure<Int>(match).recoverIf({ it.message == "match" }, { 99 }).getOrThrow() shouldBe 99
         Result.failure<Int>(nomatch).recoverIf({ it.message == "match" }, { 99 }).exceptionOrNull() shouldBe nomatch
         Result.success(1).recoverIf({ true }, { 99 }).getOrThrow() shouldBe 1
      }

      test("recoverNullIf converts a matching failure into success(null)") {
         Result.failure<Int>(RuntimeException("match")).recoverNullIf { it.message == "match" }
            .getOrThrow().shouldBeNull()
         val ex = RuntimeException("no")
         Result.failure<Int>(ex).recoverNullIf { it.message == "match" }.exceptionOrNull() shouldBe ex
      }

      test("mapFailure transforms the throwable but preserves success") {
         val original = RuntimeException("a")
         Result.failure<Int>(original).mapFailure { IllegalStateException(it.message) }
            .exceptionOrNull().shouldBeFailureMessage("a")
         Result.success(5).mapFailure { IllegalStateException("never") }.getOrThrow() shouldBe 5
      }

      test("mapIf applies f when predicate matches, else returns original") {
         Result.success(1).mapIf({ it > 0 }, { it * 10 }).getOrThrow() shouldBe 10
         Result.success(-1).mapIf({ it > 0 }, { it * 10 }).getOrThrow() shouldBe -1
      }

      test("mapIf with branched f, g picks one or the other") {
         Result.success(1).mapIf({ it > 0 }, { "pos" }, { "neg" }).getOrThrow() shouldBe "pos"
         Result.success(-1).mapIf({ it > 0 }, { "pos" }, { "neg" }).getOrThrow() shouldBe "neg"
      }

      test("mapIfNotNull skips null, applies fn to non-null") {
         Result.success<String?>("x").mapIfNotNull { it.length }.getOrThrow() shouldBe 1
         Result.success<String?>(null).mapIfNotNull { it.length }.getOrThrow().shouldBeNull()
      }

      test("mapIfNull replaces null with fn(), passes through non-null") {
         Result.success<String?>("x").mapIfNull { "default" }.getOrThrow() shouldBe "x"
         Result.success<String?>(null).mapIfNull { "default" }.getOrThrow() shouldBe "default"
      }

      test("mapCatchingIfNotNull captures fn-thrown exceptions as Result.failure") {
         Result.success<String?>("x").mapCatchingIfNotNull { it.length }.getOrThrow() shouldBe 1
         val res: Result<Int?> = Result.success<String?>("x").mapCatchingIfNotNull {
            throw RuntimeException("oops")
         }
         res.shouldBeFailure()
      }

      test("failureIfNull turns null into Result.failure, non-null into success") {
         val nonNull: String? = "x"
         nonNull.failureIfNull().getOrThrow() shouldBe "x"
         val nullVal: String? = null
         nullVal.failureIfNull().shouldBeFailure()
      }

      test("failIfNull(message) attaches the message and fails on null success") {
         Result.success<String?>("x").failIfNull("nope").getOrThrow() shouldBe "x"
         Result.success<String?>(null).failIfNull("nope").exceptionOrNull()?.message shouldBe "nope"
      }

      test("zip3 combines three successes; fails on first failure") {
         Result.success(1).zip(Result.success("a"), Result.success(true)).getOrThrow() shouldBe Triple(1, "a", true)
         val ex = RuntimeException()
         Result.success(1).zip(Result.failure<String>(ex), Result.success(true)).exceptionOrNull() shouldBe ex
      }

      test("combine chains the second result's value onto the first") {
         Result.success(1).combine { Result.success("v$it") }.getOrThrow() shouldBe (1 to "v1")
      }

      test("sequence collects all successes; fails on first failure") {
         listOf(Result.success(1), Result.success(2), Result.success(3))
            .sequence().getOrThrow() shouldBe listOf(1, 2, 3)

         val ex = RuntimeException()
         listOf(Result.success(1), Result.failure<Int>(ex), Result.success(3))
            .sequence().exceptionOrNull() shouldBe ex
      }

      test("collect partitions a list of Results") {
         val ex = RuntimeException("boom")
         val (errors, values) = listOf(Result.success(1), Result.failure(ex), Result.success(3)).collect()
         errors shouldBe listOf(ex)
         values shouldBe listOf(1, 3)
      }

      test("collectSuccess returns only the successful values") {
         listOf(Result.success(1), Result.failure<Int>(RuntimeException()), Result.success(3))
            .collectSuccess() shouldBe listOf(1, 3)
      }

      test("collectFailure returns only the errors") {
         val ex = RuntimeException("boom")
         listOf(Result.success(1), Result.failure<Int>(ex), Result.success(3))
            .collectFailure() shouldBe listOf(ex)
      }

      test("Result<List>.firstOrNull(p) finds the first matching element or null") {
         Result.success(listOf(1, 2, 3)).firstOrNull { it > 1 }.getOrThrow() shouldBe 2
         Result.success(listOf(1, 2, 3)).firstOrNull { it > 99 }.getOrThrow().shouldBeNull()
      }

      test("Result<List>.mapElements applies f to each element") {
         Result.success(listOf(1, 2, 3)).mapElements { it * 10 }.getOrThrow() shouldBe listOf(10, 20, 30)
      }

      test("Result<List>.filterElements keeps matching elements") {
         Result.success(listOf(1, 2, 3, 4)).filterElements { it % 2 == 0 }
            .getOrThrow() shouldBe listOf(2, 4)
      }

      test("List.firstOrFailure returns failure if no match") {
         listOf(1, 2, 3).firstOrFailure { it > 1 }.getOrThrow() shouldBe 2
         listOf(1, 2, 3).firstOrFailure { it > 99 }.shouldBeFailure()
      }

      test("Result<Bool>.getOrFalse returns the value or false on failure") {
         Result.success(true).getOrFalse() shouldBe true
         Result.success(false).getOrFalse() shouldBe false
         Result.failure<Boolean>(RuntimeException()).getOrFalse() shouldBe false
      }

      test("Result<Bool>.getOrTrue returns the value or true on failure") {
         Result.success(false).getOrTrue() shouldBe false
         Result.success(true).getOrTrue() shouldBe true
         Result.failure<Boolean>(RuntimeException()).getOrTrue() shouldBe true
      }

      test("mapIfEmpty replaces only an empty list") {
         Result.success(emptyList<Int>()).mapIfEmpty { listOf(99) }.getOrThrow() shouldBe listOf(99)
         Result.success(listOf(1, 2)).mapIfEmpty { listOf(99) }.getOrThrow() shouldBe listOf(1, 2)
      }
   }

   private fun Throwable?.shouldBeFailureMessage(expected: String) {
      this?.message shouldBe expected
   }
}
