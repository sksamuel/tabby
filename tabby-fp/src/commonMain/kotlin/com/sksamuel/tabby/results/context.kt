package com.sksamuel.tabby.results

fun main() {
   val r = Result.success("foo")
   val f = Result.failure(Exception("Foo"))
}

