package com.sksamuel.tabby.jackson

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.none
import com.sksamuel.tabby.option.some
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TabbyModuleTest : FunSpec({

   val mapper = jacksonObjectMapper()
   mapper.registerModule(TabbyModule)

   test("deserialize option recursively") {
      data class Foo(val a: Option<String>, val b: Option<Int>, val c: Option<Foo>)
      mapper.readValue<Foo>("""{"a": "a", "b": null, "c": null}""") shouldBe Foo(Option.Some("a"), none, none)
      mapper.readValue<Foo>("""{"a": null, "b": null, "c": { "a": "a", "b": 1, "c": null }}""") shouldBe
         Foo(none, none, Option.Some(Foo("a".some(), 1.some(), none)))
   }

   test("serialize option") {
      data class Foo(val a: Option<String>, val b: Option<Int>, val c: Option<Boolean>)
      mapper.writeValueAsString(Foo("foo".some(), none, true.some())) shouldBe """{"a":"foo","b":null,"c":true}"""
      mapper.writeValueAsString(Foo(none, none, true.some())) shouldBe """{"a":null,"b":null,"c":true}"""
      mapper.writeValueAsString(Foo(none, 123.some(), none)) shouldBe """{"a":null,"b":123,"c":null}"""
   }

   test("serialize recursively") {
      data class Foo(val a: Option<String>, val b: Option<Int>, val c: Option<Foo>)
      mapper.writeValueAsString(Foo("foo".some(), none, none)) shouldBe """{"a":"foo","b":null,"c":null}"""
      mapper.writeValueAsString(Foo("foo".some(), none, Foo("bar".some(), 1.some(), none).some())) shouldBe
         """{"a":"foo","b":null,"c":{"a":"bar","b":1,"c":null}}"""
   }
})
