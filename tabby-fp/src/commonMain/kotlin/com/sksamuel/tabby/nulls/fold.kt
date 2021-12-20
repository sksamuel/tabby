package com.sksamuel.tabby.nulls

@Deprecated("Use foldNull to avoid import clashes with library fold methods")
inline fun <A, B> A?.fold(ifNull: () -> B, ifNotNull: (A) -> B): B {
   return if (this == null) ifNull() else ifNotNull(this)
}

inline fun <A, B> A?.foldNull(ifNull: () -> B, ifNotNull: (A) -> B): B {
   return if (this == null) ifNull() else ifNotNull(this)
}
