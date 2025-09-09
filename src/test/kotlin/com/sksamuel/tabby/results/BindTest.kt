package com.sksamuel.tabby.results

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

class BindTest : FunSpec() {
   init {
      test("runCatching with bind") {
         val a = "a".success()
         val b = "b".success()
         catching {
            a.bind() + b.bind()
         } shouldBe "ab".success()
      }
      test("catching rethrows things that shouldn't be caught") {
         shouldThrow<VirtualMachineError> {
            catching { throw OutOfMemoryError("don't catch me") }
         }
         shouldThrow<ThreadDeath> {
            catching { throw ThreadDeath() }
         }
         shouldThrow<LinkageError> {
            catching { throw LinkageError("don't catch me") }
         }
         shouldThrow<kotlin.coroutines.cancellation.CancellationException> {
            catching { throw kotlin.coroutines.cancellation.CancellationException("don't catch me") }
         }
         shouldThrow<kotlinx.coroutines.CancellationException> {
            catching { throw kotlinx.coroutines.CancellationException("don't catch me") }
         }
         shouldThrow<java.util.concurrent.CancellationException> {
            catching { throw java.util.concurrent.CancellationException("don't catch me") }
         }
         shouldThrow<TimeoutCancellationException> {
            withTimeout(1L) { delay(1000L) }
         }
         shouldThrow<InterruptedException> {
            catching { throw InterruptedException("don't catch me") }
         }
         val catchMe = RuntimeException("catch me")
         catching { throw catchMe } shouldBeFailure catchMe
      }
   }
}
