package com.sksamuel.tabby.io

fun <E, T> List<IO<E, T>>.collectSuccess(): Task<List<T>> = IO.collectSuccess(this)

/**
 * Returns an effect that contains the results of the given effects. If any of the given
 * effects fails, this effect will fail and any successes will be dropped.
 */
fun <E, T> List<IO<E, T>>.traverse(): IO<E, List<T>> = IO.traverse(this)
