package com.sksamuel.tabby.results

@Deprecated("use mapElements", ReplaceWith("mapElements(f)"))
inline fun <A, B> Result<List<A>>.mapList(f: (A) -> B): Result<List<B>> = mapElements(f)

/**
 * If the [Result] is a success, maps over each element of the contained list, otherwise returns this.
 */
inline fun <A, B> Result<List<A>>.mapElements(f: (A) -> B): Result<List<B>> = map { it.map(f) }

/**
 * If the [Result] is a success, maps over each element of the contained list, otherwise returns this.
 * If the function returns null, the result is ignored and filtered.
 */
inline fun <A, B> Result<List<A>>.mapElementsNotNull(f: (A) -> B?): Result<List<B>> = map { it.mapNotNull(f) }

/**
 * If the [Result] is a success, filters the contained list, otherwise returns this.
 */
inline fun <A> Result<List<A>>.filterElements(p: (A) -> Boolean): Result<List<A>> = map { it.filter(p) }

fun <A> Result<List<A>>.firstOrNull(f: (A) -> Boolean): Result<A?> = map { it.firstOrNull(f) }

fun <A> Result<List<A>>.firstOrNull(): Result<A?> = map { it.firstOrNull() }

inline fun <A> Result<List<A>>.mapIfEmpty(f: () -> List<A>): Result<List<A>> =
   map { if (it.isEmpty()) f() else it }

inline fun <A> Result<List<A>>.flatMapIfEmpty(f: () -> Result<List<A>>): Result<List<A>> =
   flatMap { if (it.isEmpty()) f() else it.success() }

fun <A> List<A>.firstOrFailure(p: (A) -> Boolean) = runCatching { this.first(p) }
