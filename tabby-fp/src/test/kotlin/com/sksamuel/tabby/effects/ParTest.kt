package com.sksamuel.tabby.effects

import com.sksamuel.tabby.*
import com.sksamuel.tabby.results.success
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class ParTest : FunSpec() {
   init {

      fun successEffect(value: String): suspend () -> Result<String> = {
         value.success()
      }

      fun failureEffect(message: String): suspend () -> Result<String> = {
         Result.failure(RuntimeException(message))
      }

      val letters = ('a'..'z').map { it.toString() }

      suspend fun invokeParN(size: Int, effects: List<suspend () -> Result<String>>): Result<Any> {
         return when (size) {
            2 -> parN(effects[0], effects[1])
            3 -> parN(effects[0], effects[1], effects[2])
            4 -> parN(effects[0], effects[1], effects[2], effects[3])
            5 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4])
            6 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5])
            7 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6])
            8 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7])
            9 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8])
            10 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9])
            11 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9], effects[10])
            12 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9], effects[10], effects[11])
            13 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9], effects[10], effects[11], effects[12])
            14 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9], effects[10], effects[11], effects[12], effects[13])
            15 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9], effects[10], effects[11], effects[12], effects[13], effects[14])
            16 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9], effects[10], effects[11], effects[12], effects[13], effects[14], effects[15])
            17 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9], effects[10], effects[11], effects[12], effects[13], effects[14], effects[15], effects[16])
            18 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9], effects[10], effects[11], effects[12], effects[13], effects[14], effects[15], effects[16], effects[17])
            19 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9], effects[10], effects[11], effects[12], effects[13], effects[14], effects[15], effects[16], effects[17], effects[18])
            20 -> parN(effects[0], effects[1], effects[2], effects[3], effects[4], effects[5], effects[6], effects[7], effects[8], effects[9], effects[10], effects[11], effects[12], effects[13], effects[14], effects[15], effects[16], effects[17], effects[18], effects[19])
            else -> throw IllegalArgumentException("Unsupported size: $size")
         }
      }

      // Helper function to extract values from any tuple type
      fun extractTupleValues(tuple: Any): List<String> {
         return when (tuple) {
            is Pair<*, *> -> listOf(tuple.first as String, tuple.second as String)
            is Triple<*, *, *> -> listOf(tuple.first as String, tuple.second as String, tuple.third as String)
            is Tuple4<*, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String)
            is Tuple5<*, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String)
            is Tuple6<*, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String)
            is Tuple7<*, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String)
            is Tuple8<*, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String)
            is Tuple9<*, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String)
            is Tuple10<*, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String)
            is Tuple11<*, *, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String, tuple.k as String)
            is Tuple12<*, *, *, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String, tuple.k as String, tuple.l as String)
            is Tuple13<*, *, *, *, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String, tuple.k as String, tuple.l as String, tuple.m as String)
            is Tuple14<*, *, *, *, *, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String, tuple.k as String, tuple.l as String, tuple.m as String, tuple.n as String)
            is Tuple15<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String, tuple.k as String, tuple.l as String, tuple.m as String, tuple.n as String, tuple.o as String)
            is Tuple16<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String, tuple.k as String, tuple.l as String, tuple.m as String, tuple.n as String, tuple.o as String, tuple.p as String)
            is Tuple17<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String, tuple.k as String, tuple.l as String, tuple.m as String, tuple.n as String, tuple.o as String, tuple.p as String, tuple.q as String)
            is Tuple18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String, tuple.k as String, tuple.l as String, tuple.m as String, tuple.n as String, tuple.o as String, tuple.p as String, tuple.q as String, tuple.r as String)
            is Tuple19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String, tuple.k as String, tuple.l as String, tuple.m as String, tuple.n as String, tuple.o as String, tuple.p as String, tuple.q as String, tuple.r as String, tuple.s as String)
            is Tuple20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *> -> listOf(tuple.a as String, tuple.b as String, tuple.c as String, tuple.d as String, tuple.e as String, tuple.f as String, tuple.g as String, tuple.h as String, tuple.i as String, tuple.j as String, tuple.k as String, tuple.l as String, tuple.m as String, tuple.n as String, tuple.o as String, tuple.p as String, tuple.q as String, tuple.r as String, tuple.s as String, tuple.t as String)
            else -> throw IllegalArgumentException("Unsupported tuple type: ${tuple::class}")
         }
      }

      fun createEffects(size: Int, failAtIndex: Int = -1): List<suspend () -> Result<String>> {
         return (0 until size).map { i ->
            if (i == failAtIndex) failureEffect("boom") else successEffect(letters[i])
         }
      }

      (2..20).forEach { size ->
         test("parN($size) should work correctly") {
            val effects = createEffects(size)
            val expectedValues = letters.take(size)

            val result = invokeParN(size, effects)
            val tuple = result.getOrThrow()
            val actualValues = extractTupleValues(tuple)

            actualValues shouldBe expectedValues
         }

         test("parN($size) should short circuit on failure") {
            val failureIndex = if (size == 2) 1 else 2
            val effects = createEffects(size, failureIndex)

            val result = invokeParN(size, effects)

            result.isFailure shouldBe true
            result.exceptionOrNull()?.message shouldBe "boom"
         }
      }

      test("parN should work with mixed types") {
         val result = parN(
            { delay(10); 1.success() },
            { delay(10); "string".success() },
            { delay(10); 3.14.success() },
            { delay(10); true.success() },
            { delay(10); listOf(1, 2, 3).success() }
         )

         val tuple = result.getOrThrow()
         tuple.a shouldBe 1
         tuple.b shouldBe "string"
         tuple.c shouldBe 3.14
         tuple.d shouldBe true
         tuple.e shouldBe listOf(1, 2, 3)
      }

      test("parN should run in parallel (performance test)").config(timeout = 200.milliseconds) {
         val result = parN(
            { delay(50); "a".success() },
            { delay(50); "b".success() },
            { delay(50); "c".success() },
            { delay(50); "d".success() },
            { delay(50); "e".success() },
            { delay(50); "f".success() }
         )
         result.getOrThrow() shouldBe Tuple6("a", "b", "c", "d", "e", "f")
      }
   }
}
