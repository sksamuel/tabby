package com.sksamuel.tabby.nulls

object Nulls

inline fun <A, B, R> Nulls.mapN(
   a: A?,
   b: B?,
   fn: (A, B) -> R,
): R? {
   return if (a == null || b == null) null else fn(a, b)
}

inline fun <A, B, C, R> Nulls.mapN(
   a: A?,
   b: B?,
   c: C?,
   fn: (A, B, C) -> R,
): R? {
   return if (a == null || b == null || c == null) null else fn(a, b, c)
}

inline fun <A, B, C, D, R> Nulls.mapN(
   a: A?,
   b: B?,
   c: C?,
   d: D?,
   fn: (A, B, C, D) -> R,
): R? {
   return if (a == null || b == null || c == null || d == null) null else fn(a, b, c, d)
}

inline fun <A, B, C, D, E, R> Nulls.mapN(
   a: A?,
   b: B?,
   c: C?,
   d: D?,
   e: E?,
   fn: (A, B, C, D, E) -> R,
): R? {
   return if (a == null || b == null || c == null || d == null || e == null) null else fn(a, b, c, d, e)
}

inline fun <A, B, C, D, E, F, R> Nulls.mapN(
   a: A?,
   b: B?,
   c: C?,
   d: D?,
   e: E?,
   f: F?,
   fn: (A, B, C, D, E, F) -> R,
): R? {
   return if (a == null || b == null || c == null || d == null || e == null || f == null) null else fn(a, b, c, d, e, f)
}


inline fun <A, B, C, D, E, F, G, R> Nulls.mapN(
   a: A?,
   b: B?,
   c: C?,
   d: D?,
   e: E?,
   f: F?,
   g: G?,
   fn: (A, B, C, D, E, F, G) -> R,
): R? {
   return if (a == null || b == null || c == null || d == null || e == null || f == null || g == null) null
   else fn(a, b, c, d, e, f, g)
}
