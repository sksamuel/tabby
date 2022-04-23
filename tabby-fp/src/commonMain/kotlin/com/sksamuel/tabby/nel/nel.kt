package com.sksamuel.tabby.nel

data class NonEmptyList<A> private constructor(private val values: List<A>) {

   companion object {
      operator fun <A> invoke(a: A): NonEmptyList<A> = NonEmptyList(listOf(a))
      operator fun <A> invoke(a: A, vararg rest: A): NonEmptyList<A> = NonEmptyList(listOf(a) + rest)
      fun <A> unsafe(values: List<A>): NonEmptyList<A> = NonEmptyList(values)
   }

   fun head(): A = values.first()
   fun last(): A = values.last()
   fun tail(): List<A> = values.drop(1)

   fun drop(k: Int): List<A> = values.drop(k)
   fun dropLast(k: Int): List<A> = values.dropLast(k)
   fun indexOf(a: A): Int = values.indexOf(a)
   fun take(k: Int): NonEmptyList<A> = NonEmptyList(values.take(k))

   operator fun plus(other: NonEmptyList<A>): NonEmptyList<A> = NonEmptyList(values + other.values)

   fun asList(): List<A> = values

   fun <B> map(f: (A) -> B): NonEmptyList<B> = NonEmptyList(values.map(f))
   fun flatMap(f: (A) -> NonEmptyList<A>): NonEmptyList<A> = map(f).flatten()
}

fun <A> NonEmptyList<NonEmptyList<A>>.flatten(): NonEmptyList<A> = NonEmptyList.unsafe(asList().flatMap { it.asList() })
