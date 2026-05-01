package com.sksamuel.tabby.tristate

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class TristateTest : FunSpec() {
   init {

      test("isSome / isNone / isUnspecified") {
         Tristate.Some(1).isSome() shouldBe true
         Tristate.Some(1).isNone() shouldBe false
         Tristate.Some(1).isUnspecified() shouldBe false

         Tristate.None.isSome() shouldBe false
         Tristate.None.isNone() shouldBe true
         Tristate.None.isUnspecified() shouldBe false

         Tristate.Unspecified.isSome() shouldBe false
         Tristate.Unspecified.isNone() shouldBe false
         Tristate.Unspecified.isUnspecified() shouldBe true
      }

      test("map applies f only to Some") {
         (Tristate.Some(1).map { it + 1 }) shouldBe Tristate.Some(2)
         (Tristate.None.map { x: Int -> x + 1 }) shouldBe Tristate.None
         (Tristate.Unspecified.map { x: Int -> x + 1 }) shouldBe Tristate.Unspecified
      }

      test("getValueOrNull returns value for Some, null for None and Unspecified") {
         Tristate.Some(1).getValueOrNull() shouldBe 1
         Tristate.None.getValueOrNull().shouldBeNull()
         Tristate.Unspecified.getValueOrNull().shouldBeNull()
      }

      test("getOrThrow returns value for Some, null for None, throws for Unspecified") {
         Tristate.Some(1).getOrThrow() shouldBe 1
         Tristate.None.getOrThrow().shouldBeNull()
         shouldThrow<IllegalStateException> { Tristate.Unspecified.getOrThrow() }
      }

      test("getValueOrThrow returns value for Some, throws for None and Unspecified") {
         Tristate.Some(1).getValueOrThrow() shouldBe 1
         shouldThrow<IllegalStateException> { Tristate.None.getValueOrThrow() }
         shouldThrow<IllegalStateException> { Tristate.Unspecified.getValueOrThrow() }
      }

      test("fold dispatches to the right branch") {
         Tristate.Some(1).fold({ "v=$it" }, { "none" }, { "unspec" }) shouldBe "v=1"
         Tristate.None.fold({ "v=$it" }, { "none" }, { "unspec" }) shouldBe "none"
         Tristate.Unspecified.fold({ "v=$it" }, { "none" }, { "unspec" }) shouldBe "unspec"
      }

      test("getValueOrElse returns value or alternative") {
         Tristate.Some(1).getValueOrElse { 99 } shouldBe 1
         (Tristate.None as Tristate<Int>).getValueOrElse { 99 } shouldBe 99
         (Tristate.Unspecified as Tristate<Int>).getValueOrElse { 99 } shouldBe 99
      }

      test("toTristate produces Some for non-null and None for null (NEVER Unspecified)") {
         "x".toTristate() shouldBe Tristate.Some("x")
         (null as String?).toTristate() shouldBe Tristate.None
      }
   }
}
