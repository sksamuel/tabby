package com.sksamuel.tabby.jackson

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sksamuel.tabby.tristate.Tristate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TabbyModuleTristateTest : FunSpec({

   val mapper = jacksonObjectMapper()
   mapper.registerModule(TabbyModule)

   test("deserialize tristate") {
      data class Foo(val a: Tristate<String>, val b: Tristate<Int>, val c: Tristate<Boolean>)

      mapper.readValue<Foo>("""{"a": "a", "b": null}""") shouldBe Foo(
         Tristate.Some("a"),
         Tristate.None,
         Tristate.Unspecified
      )

      mapper.readValue<Foo>("""{"b": null, "c": true}""") shouldBe Foo(
         Tristate.Unspecified,
         Tristate.None,
         Tristate.Some(true)
      )
   }

   test("deserialize tristate recursively") {
      data class Foo(val a: Tristate<Foo>, val b: Tristate<Int>, val c: Tristate<Boolean>)

      mapper.readValue<Foo>("""{"a": { "a": null, "c": true }, "b": null}""") shouldBe Foo(
         Tristate.Some(Foo(Tristate.None, Tristate.Unspecified, Tristate.Some(true))),
         Tristate.None,
         Tristate.Unspecified
      )
   }

   test("serialize tristate") {

      data class Foo(val a: Tristate<String>, val b: Tristate<Int>, val c: Tristate<Boolean>)

      mapper.writeValueAsString(
         Foo(
            Tristate.Some("foo"),
            Tristate.None,
            Tristate.Some(true)
         )
      ) shouldBe """{"a":"foo","b":null,"c":true}"""

      mapper.writeValueAsString(
         Foo(
            Tristate.Unspecified,
            Tristate.None,
            Tristate.Some(true)
         )
      ) shouldBe """{"b":null,"c":true}"""

      mapper.writeValueAsString(
         Foo(
            Tristate.None,
            Tristate.Some(123),
            Tristate.Unspecified
         )
      ) shouldBe """{"a":null,"b":123}"""
   }

   test("serialize tristate recursively") {
      data class Foo(val a: Tristate<String>, val b: Tristate<Int>, val c: Tristate<Foo>)

      mapper.writeValueAsString(
         Foo(
            Tristate.Some("foo"),
            Tristate.None,
            Tristate.Some(
               Foo(
                  Tristate.None,
                  Tristate.Some(3122),
                  Tristate.Unspecified
               )
            )
         )
      ) shouldBe """{"a":"foo","b":null,"c":{"a":null,"b":3122}}"""
   }
})
