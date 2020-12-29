package com.sksamuel.tabby.io

import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.none
import com.sksamuel.tabby.option.some
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.system.exitProcess

class OrElseTest : FunSpec() {
   init {

      test("IO<E, Option<T>>.orElse should not be invoked if the result is a some") {
         IO.effect { "foo".some() }.orElse {
            exitProcess(1)
         }.run().getRightUnsafe() shouldBe "foo"
      }

      test("IO<E, Option<T>>.orElse should be invoked if the result is a none") {
         IO.effect { none }.orElse { IO.success("boo") }.run().getRightUnsafe() shouldBe "boo"
      }

      test("IO<E, Option<T>>.orElse should use error on rhs if that fails") {
         IO.effect { none }.orElse {
            IO.effect { error("kabam") }
         }.run().getLeftUnsafe().message shouldBe "kabam"
      }

      test("IO<E, Option<T>>.orElse should not be invoked if the IO fails ") {
         fun value(): Option<String> = error("boom")
         IO.effect { value() }.orElse {
            exitProcess(1)
         }.run().getLeftUnsafe().message shouldBe "boom"
      }
   }
}