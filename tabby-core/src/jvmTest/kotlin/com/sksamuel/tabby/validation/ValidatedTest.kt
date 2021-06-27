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

      test("parsing non blank strings to domain object") {
         Parser<String>()
            .notBlank { "cannot be blank" }
            .map { Foo("input") }
            .parse("    ") shouldBe Validated.invalid("cannot be blank")
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

      test("parser should support booleans") {
         val p = Parser<String>().boolean { "not a boolean" }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("not a boolean")
         p.parse("true").getValueUnsafe() shouldBe true
         p.parse("false").getValueUnsafe() shouldBe false
      }

      test("parser should support ints") {
         val p = Parser<String>().long { "not an int" }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("not an int")
         p.parse("12345").getValueUnsafe() shouldBe 12345
      }

      test("parser should support ints with nullable pass through") {
         val p = Parser<String>().long { "not an int" }.nullable()
         p.parse("12345").getValueUnsafe() shouldBe 12345
         p.parse(null).getValueUnsafe() shouldBe null
      }

      test("parser should support ints with nullable failure message") {
         val p = Parser<String>().long { "not an int" }.notNull { "cannot be null" }
         p.parse("12345").getValueUnsafe() shouldBe 12345
         p.parse(null).getErrorsUnsafe() shouldBe listOf("cannot be null")
      }

      test("parser should support longs") {
         val p = Parser<String>().long { "not a long" }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("not a long")
         p.parse("12345").getValueUnsafe() shouldBe 12345L
      }

      test("parser should support doubles") {
         val p = Parser<String>().double { "not a double" }.map { Width(it) }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("not a double")
         p.parse("123.45").getValueUnsafe() shouldBe Width(123.45)
      }

      test("parser should support doubles with nullable pass through") {
         val p = Parser<String>().double { "not a double" }.nullable()
         p.parse("123.45").getValueUnsafe() shouldBe 123.45
         p.parse(null).getValueUnsafe() shouldBe null
      }

      test("parser should support doubles with nullable failure message") {
         val p = Parser<String>().double { "not a double" }.notNull { "cannot be null" }
         p.parse("123.45").getValueUnsafe() shouldBe 123.45
         p.parse(null).getErrorsUnsafe() shouldBe listOf("cannot be null")
      }

      test("parser should support floats") {
         val p = Parser<String>().float { "not a float" }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("not a float")
         p.parse("123.45").getValueUnsafe() shouldBe 123.45F
      }

      test("repeated parser") {
         val ps = Parser<String>().map { Foo(it) }.repeated()
         ps.parse(listOf("a", "b")) shouldBe listOf(Foo("a"), Foo("b")).valid()
      }

      test("not null") {
         val p = Parser<String>().map { Foo(it) }.notNull { "cannot be null" }
         p.parse(null) shouldBe "cannot be null".invalid()
      }

      test("min length") {
         val p = Parser<String>().minlen(4) { "too short" }.map { Foo(it) }
         p.parse("abc") shouldBe "too short".invalid()
         p.parse("abcd") shouldBe Foo("abcd").valid()
      }

      test("max length") {
         val p = Parser<String>().maxlen(4) { "too long" }.map { Foo(it) }
         p.parse("abcde") shouldBe "too long".invalid()
         p.parse("abcd") shouldBe Foo("abcd").valid()
         p.parse("abc") shouldBe Foo("abc").valid()
      }

      test("trim") {
         val p = Parser<String>().trim().map { Foo(it) }
         p.parse(" abcd ") shouldBe Foo("abcd").valid()
         p.parse("abc    ") shouldBe Foo("abc").valid()
      }

      test("filter") {
         val p = Parser<String>().filter(String::isNotEmpty) { "boom" }.map { Foo(it) }
         p.parse("") shouldBe "boom".invalid()
         p.parse("abc") shouldBe Foo("abc").valid()
      }
   }
}
