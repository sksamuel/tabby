package com.sksamuel.tabby.option

import kotlin.experimental.ExperimentalTypeInference

/**
 * Returns the first element of this List wrapped in a Some if the list is non empty,
 * otherwise returns None.
 */
fun <A> List<A>.firstOrNone(): Option<A> = this.firstOrNull().toOption()

fun <A> List<A>.firstOrNone(p: (A) -> Boolean) = this.firstOrNull(p).toOption()

fun <A> List<Option<A>>.filterNotEmpty(): List<A> = this.mapNotNull { it.orNull() }

inline fun <A, B> List<A>.mapNotEmpty(f: (A) -> Option<B>): List<B> =
   this.mapNotNull { f(it).orNull() }

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <T, U : Any> List<T>.flatMap(f: (T) -> Option<U>): List<U> = mapNotNull { f(it).orNull() }

/**
 * Returns a new list that contains just the values of any [Option.Some] instances in this list.
 *
 * In other words, listOf(Some(1), None, Some(2)) becomes listOf(1,2).
 */
fun <A : Any> List<Option<A>>.flatten() = mapNotNull { it.orNull() }
