package com.sksamuel.tabby.validation

import io.kotest.core.spec.style.FunSpec

@JvmInline
value class Foo(val value: String)

class ValidatedTest : FunSpec() {
   init {
      test("parser should support default") {
         val p = Parser<String>()
            .mapIfNotNull { Foo(it) }
            .default { Foo("foo") }
         val result: Foo = p.parse("foo").getUnsafe()
      }
      test("parser should support default on nullable inputs") {
         val p = Parser<String?>()
            .mapIfNotNull { Foo(it) }
            .default { Foo("foo") }
         val result: Foo = p.parse("foo").getUnsafe()
      }
   }
}
