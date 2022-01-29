package com.sksamuel.tabby.results

fun <T, U> Result<T?>.foldNull(ifNull: () -> U, ifNotNull: (T) -> U, ifError: (Throwable) -> U) {
   this.fold({ if (it == null) ifNull() else ifNotNull(it) }, ifError)
}
