package com.sksamuel.tabby.results

fun <T> T.success() = Result.success(this)

fun Throwable.failure() = Result.failure<Nothing>(this)

fun String.failure() = Exception(this).failure()
