package com.sksamuel.tabby.results

@Deprecated("use mapElements")
inline fun <A, B> Result<List<A>>.mapList(f: (A) -> B): Result<List<B>> = mapElements(f)

/**
 * Maps over each element of this list, if the [Result] is a success, otherwise returns this.
 */
inline fun <A, B> Result<List<A>>.mapElements(f: (A) -> B): Result<List<B>> = map { it.map(f) }

fun <A> Result<List<A>>.firstOrNull(f: (A) -> Boolean): Result<A?> = map { it.firstOrNull(f) }

fun <A> Result<List<A>>.firstOrNull(): Result<A?> = map { it.firstOrNull() }
