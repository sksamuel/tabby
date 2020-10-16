//package com.sksamuel.tabby.io
//
//import com.sksamuel.tabby.right
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
//import io.kotest.matchers.shouldBe
//
//class RetryTest : FunSpec() {
//   init {
//
//      test("io retry should be invoked up to max times") {
//         var iterations = 0
//         IO.effect {
//            iterations++
//            error("boom")
//         }.retry(5, 10).run()
//         iterations shouldBe 5
//      }
//
//      test("io retry should return if successful") {
//         var iterations = 0
//         IO.effect {
//            iterations++
//            "foo"
//         }.retry(5, 10).run() shouldBe "foo".right()
//         iterations shouldBe 1
//      }
//
//      test("io retry should not retry once successful") {
//         var iterations = 0
//         IO.effect {
//            iterations++
//            if (iterations > 2)
//               "foo"
//            else
//               error("boom")
//         }.retry(5, 10).run()
//         iterations shouldBe 3
//      }
//
//      test("io should delay between attempts") {
//         var iterations = 0
//         val start = 0
//         IO.effect {
//            iterations++
//            error("boom")
//         }.retry(5, 250).run()
//         iterations shouldBe 5
//         System.currentTimeMillis() - start shouldBeGreaterThanOrEqual 250 * 5
//      }
//   }
//}
