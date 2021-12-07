package com.sksamuel.tabby.results

fun <A, B> Result<List<A>>.mapList(f: (A) -> B): Result<List<B>> = map { it.map(f) }
