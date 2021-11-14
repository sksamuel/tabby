//package com.sksamuel.tabby.effects
//
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.shouldBe
//import kotlin.system.exitProcess
//
//class RecoverTest : FunSpec() {
//   init {
//
//      test("IO<T>.recover should be invoked if the result is a failure") {
//         IO.effect { error("a") }.recover { IO.pure("b") }.run().getValueUnsafe() shouldBe "b"
//      }
//
//      test("IO<T>.recover should use error on rhs if both fail") {
//         IO.effect { error("a") }.recover { IO.effect { error("b") } }.run().getErrorUnsafe().message shouldBe "b"
//      }
//
//      test("IO<T>.recoverEffect should not be invoked if the result is success") {
//         IO.effect { "a" }.recoverEffect { exitProcess(1) }.run().getValueUnsafe() shouldBe "a"
//      }
//
//      test("IO<T>.recoverEffect should use error on rhs if both fail") {
//         IO.effect { error("a") }.recoverEffect { error("b") }.run().getErrorUnsafe().message shouldBe "b"
//      }
//   }
//}
