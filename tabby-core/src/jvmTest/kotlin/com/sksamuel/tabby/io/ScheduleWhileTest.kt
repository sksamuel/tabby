//package com.sksamuel.tabby.io
//
//import com.sksamuel.tabby.effects.IO
//import com.sksamuel.tabby.effects.Schedule
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.shouldBe
//import java.util.concurrent.atomic.AtomicInteger
//import kotlin.time.ExperimentalTime
//
//class ScheduleWhileTest : FunSpec() {
//   init {
//
//      test("Schedule.whileTrue should run while predicate is true") {
//         val counter = AtomicInteger(0)
//         val effect = IO { counter.incrementAndGet() }
//         effect.repeat(Schedule.whileTrue { it < 10 }).run()
//         counter.get() shouldBe 10
//      }
//
//      test("Schedule.whileTrue should run while predicate is true or an error is reached") {
//         var counter = 0
//         val effect = IO.effect {
//            counter++
//            if (counter == 5) error("boom")
//            counter
//         }
//         effect.repeat(Schedule.whileTrue { it < 10 }).run()
//         counter shouldBe 5
//      }
//   }
//}
