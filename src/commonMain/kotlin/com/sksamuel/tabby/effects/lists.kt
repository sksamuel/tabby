package com.sksamuel.tabby.effects

/**
 * For an IO<List<T>>, applies the given function to each element of the list,
 * returning a new IO with the mapped list.
 */
fun <T, U> IO<List<T>>.mapList(f: (T) -> U): IO<List<U>> {
   return this.map { it.map(f) }
}
