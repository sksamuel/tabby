package com.sksamuel.tabby.effects

import com.sksamuel.tabby.results.failure
import com.sksamuel.tabby.results.success
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ScheduleOnceTest : FunSpec() {
   init {

      test("schedule once should run the effect twice, if the initial is successful") {
         var counter = 0
         repeat(Schedule.once) {
            counter++
            counter.success()
         }
         counter shouldBe 2
      }

      test("schedule once should run the effect once, if the initial run failed") {
         var counter = 0
         val effect = repeat(Schedule.once) {
            counter++
            "Boom".failure()
         }
         counter shouldBe 1
      }
   }
}
