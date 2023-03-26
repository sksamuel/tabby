package com.sksamuel.tabby.nulls

inline fun <A, B> A?.foldNull(ifNull: () -> B, ifNotNull: (A) -> B): B {
   return if (this == null) ifNull() else ifNotNull(this)
}
