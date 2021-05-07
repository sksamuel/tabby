package com.sksamuel.tabby

data class Tuple2<A, B>(
   val a: A,
   val b: B,
)

data class Tuple3<A, B, C>(
   val a: A,
   val b: B,
   val c: C,
)

data class Tuple4<A, B, C, D>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
) {
   fun <R> map(fn: (A, B, C, D) -> R): R = fn(a, b, c, d)
}

data class Tuple5<A, B, C, D, E>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
) {
   fun <R> map(fn: (A, B, C, D, E) -> R): R = fn(a, b, c, d, e)
}

data class Tuple6<A, B, C, D, E, F>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
   val f: F,
) {
   fun <R> map(fn: (A, B, C, D, E, F) -> R): R = fn(a, b, c, d, e, f)
}

data class Tuple7<A, B, C, D, E, F, G>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
   val f: F,
   val g: G,
) {
   fun <R> map(fn: (A, B, C, D, E, F, G) -> R): R = fn(a, b, c, d, e, f, g)
}

data class Tuple8<A, B, C, D, E, F, G, H>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
   val f: F,
   val g: G,
   val h: H,
)

data class Tuple9<A, B, C, D, E, F, G, H, I>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
   val f: F,
   val g: G,
   val h: H,
   val i: I,
)

data class Tuple10<A, B, C, D, E, F, G, H, I, J>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
   val f: F,
   val g: G,
   val h: H,
   val i: I,
   val j: J,
)

data class Tuple11<A, B, C, D, E, F, G, H, I, J, K>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
   val f: F,
   val g: G,
   val h: H,
   val i: I,
   val j: J,
   val k: K,
)

data class Tuple12<A, B, C, D, E, F, G, H, I, J, K, L>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
   val f: F,
   val g: G,
   val h: H,
   val i: I,
   val j: J,
   val k: K,
   val l: L,
)

data class Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
   val f: F,
   val g: G,
   val h: H,
   val i: I,
   val j: J,
   val k: K,
   val l: L,
   val m: M,
)

data class Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>(
   val a: A,
   val b: B,
   val c: C,
   val d: D,
   val e: E,
   val f: F,
   val g: G,
   val h: H,
   val i: I,
   val j: J,
   val k: K,
   val l: L,
   val m: M,
   val n: N,
)
