package com.sksamuel.tabby.effects

import com.sksamuel.tabby.results.success
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe

class UseTest : FunSpec() {
   init {

      test("use closes the resource when f succeeds") {
         val resource = Recorder()
         Result.success(resource).use { Result.success("ok") }
         resource.closed shouldBe true
      }

      test("use closes the resource when f returns a failure") {
         val resource = Recorder()
         Result.success(resource).use { Result.failure<String>(RuntimeException("f failed")) }
         resource.closed shouldBe true
      }

      test("use returns the value when f succeeds and close succeeds") {
         Result.success(Recorder()).use { "value".success() }.shouldBeSuccess() shouldBe "value"
      }

      test("use returns f's failure when f fails and close succeeds") {
         val expected = RuntimeException("f failed")
         val actual = Result.success(Recorder()).use { Result.failure<String>(expected) }
         actual.exceptionOrNull() shouldBe expected
      }

      test("use returns the close error as a Result.failure when f succeeds and close throws") {
         val closeException = RuntimeException("close failed")
         val actual = Result.success(ThrowingOnClose(closeException)).use { "value".success() }
         actual.shouldBeFailure()
         actual.exceptionOrNull() shouldBe closeException
      }
   }
}

private class Recorder : AutoCloseable {
   var closed: Boolean = false
   override fun close() {
      closed = true
   }
}

private class ThrowingOnClose(private val ex: Throwable) : AutoCloseable {
   override fun close() {
      throw ex
   }
}
