package com.sksamuel.tabby.validation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@JvmInline
value class Foo(val value: String)

@JvmInline
value class Width(val value: Double)

class ValidatedTest : FunSpec() {
   init {

      test("parsing strings to domain object") {
         Parser<String>().parse("input").map { Foo("input") } shouldBe Foo("input").valid()
      }

      test("parser should support default") {
         val p = Parser<String>()
            .mapIfNotNull { Foo(it) }
            .default { Foo("foo") }
         val result: Foo = p.parse("foo").getValueUnsafe()
      }

      test("parser should support default on nullable inputs") {
         val p = Parser<String?>()
            .mapIfNotNull { Foo(it) }
            .default { Foo("foo") }
         val result: Foo = p.parse("foo").getValueUnsafe()
      }

      test("parser should support doubles") {
         val p = Parser<String?>().double { "woo" }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("woo")
         p.parse("123.45").getValueUnsafe() shouldBe 123.45
      }

   }
}
