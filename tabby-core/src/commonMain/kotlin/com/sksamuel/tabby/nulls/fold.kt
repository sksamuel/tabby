package com.sksamuel.tabby.nulls

fun <A, B> A?.fold(ifNull: () -> B, ifNotNull: (A) -> B): B {
   return if (this == null) ifNull() else ifNotNull(this)
}
