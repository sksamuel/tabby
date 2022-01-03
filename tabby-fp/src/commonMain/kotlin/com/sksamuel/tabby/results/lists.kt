package com.sksamuel.tabby.results

fun <A, B> Result<List<A>>.mapList(f: (A) -> B): Result<List<B>> = map { it.map(f) }

fun <A> Result<List<A>>.firstOrNull(f: (A) -> Boolean): Result<A?> = map { it.firstOrNull(f) }

fun <A> Result<List<A>>.firstOrNull(): Result<A?> = map { it.firstOrNull() }
