package com.sksamuel.tabby.validation

fun <A, B, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   f: (A, B) -> R
): Validated<ERROR, R> {
   val errors = listOf(a, b).filterIsInstance<Validated.Invalid<ERROR>>().flatMap { it.errors }
   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      f(a.getUnsafe(), b.getUnsafe()).valid()
   }
}

fun <A, B, C, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   f: (A, B, C) -> R
): Validated<ERROR, R> {
   val errors = listOf(a, b, c).filterIsInstance<Validated.Invalid<ERROR>>().flatMap { it.errors }
   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe()).valid()
   }
}

fun <A, B, C, D, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   f: (A, B, C, D) -> R
): Validated<ERROR, R> {
   val errors = listOf(a, b, c, d).filterIsInstance<Validated.Invalid<ERROR>>().flatMap { it.errors }
   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe()).valid()
   }
}

fun <A, B, C, D, E, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   e: Validated<ERROR, E>,
   f: (A, B, C, D, E) -> R
): Validated<ERROR, R> {
   val errors = listOf(a, b, c, d, e).filterIsInstance<Validated.Invalid<ERROR>>().flatMap { it.errors }
   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe(), e.getUnsafe()).valid()
   }
}

fun <A, B, C, D, E, F, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   e: Validated<ERROR, E>,
   f: Validated<ERROR, F>,
   fn: (A, B, C, D, E, F) -> R
): Validated<ERROR, R> {

   val errors = listOf(a, b, c, d, e, f)
      .filterIsInstance<Validated.Invalid<ERROR>>()
      .flatMap { it.errors }

   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      fn(
         a.getUnsafe(),
         b.getUnsafe(),
         c.getUnsafe(),
         d.getUnsafe(),
         e.getUnsafe(),
         f.getUnsafe(),
      ).valid()
   }
}

fun <A, B, C, D, E, F, G, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   e: Validated<ERROR, E>,
   f: Validated<ERROR, F>,
   g: Validated<ERROR, G>,
   fn: (A, B, C, D, E, F, G) -> R
): Validated<ERROR, R> {

   val errors = listOf(a, b, c, d, e, f, g)
      .filterIsInstance<Validated.Invalid<ERROR>>()
      .flatMap { it.errors }

   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      fn(
         a.getUnsafe(),
         b.getUnsafe(),
         c.getUnsafe(),
         d.getUnsafe(),
         e.getUnsafe(),
         f.getUnsafe(),
         g.getUnsafe(),
      ).valid()
   }
}

fun <A, B, C, D, E, F, G, H, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   e: Validated<ERROR, E>,
   f: Validated<ERROR, F>,
   g: Validated<ERROR, G>,
   h: Validated<ERROR, H>,
   fn: (A, B, C, D, E, F, G, H) -> R
): Validated<ERROR, R> {

   val errors = listOf(a, b, c, d, e, f, g, h)
      .filterIsInstance<Validated.Invalid<ERROR>>()
      .flatMap { it.errors }

   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      fn(
         a.getUnsafe(),
         b.getUnsafe(),
         c.getUnsafe(),
         d.getUnsafe(),
         e.getUnsafe(),
         f.getUnsafe(),
         g.getUnsafe(),
         h.getUnsafe(),
      ).valid()
   }
}

fun <A, B, C, D, E, F, G, H, I, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   e: Validated<ERROR, E>,
   f: Validated<ERROR, F>,
   g: Validated<ERROR, G>,
   h: Validated<ERROR, H>,
   i: Validated<ERROR, I>,
   fn: (A, B, C, D, E, F, G, H, I) -> R
): Validated<ERROR, R> {

   val errors = listOf(a, b, c, d, e, f, g, h, i)
      .filterIsInstance<Validated.Invalid<ERROR>>()
      .flatMap { it.errors }

   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      fn(
         a.getUnsafe(),
         b.getUnsafe(),
         c.getUnsafe(),
         d.getUnsafe(),
         e.getUnsafe(),
         f.getUnsafe(),
         g.getUnsafe(),
         h.getUnsafe(),
         i.getUnsafe(),
      ).valid()
   }
}

fun <A, B, C, D, E, F, G, H, I, J, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   e: Validated<ERROR, E>,
   f: Validated<ERROR, F>,
   g: Validated<ERROR, G>,
   h: Validated<ERROR, H>,
   i: Validated<ERROR, I>,
   j: Validated<ERROR, J>,
   fn: (A, B, C, D, E, F, G, H, I, J) -> R
): Validated<ERROR, R> {

   val errors = listOf(a, b, c, d, e, f, g, h, i, j)
      .filterIsInstance<Validated.Invalid<ERROR>>()
      .flatMap { it.errors }

   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      fn(
         a.getUnsafe(),
         b.getUnsafe(),
         c.getUnsafe(),
         d.getUnsafe(),
         e.getUnsafe(),
         f.getUnsafe(),
         g.getUnsafe(),
         h.getUnsafe(),
         i.getUnsafe(),
         j.getUnsafe(),
      ).valid()
   }
}

fun <A, B, C, D, E, F, G, H, I, J, K, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   e: Validated<ERROR, E>,
   f: Validated<ERROR, F>,
   g: Validated<ERROR, G>,
   h: Validated<ERROR, H>,
   i: Validated<ERROR, I>,
   j: Validated<ERROR, J>,
   k: Validated<ERROR, K>,
   fn: (A, B, C, D, E, F, G, H, I, J, K) -> R
): Validated<ERROR, R> {

   val errors = listOf(a, b, c, d, e, f, g, h, i, j, k)
      .filterIsInstance<Validated.Invalid<ERROR>>()
      .flatMap { it.errors }

   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      fn(
         a.getUnsafe(),
         b.getUnsafe(),
         c.getUnsafe(),
         d.getUnsafe(),
         e.getUnsafe(),
         f.getUnsafe(),
         g.getUnsafe(),
         h.getUnsafe(),
         i.getUnsafe(),
         j.getUnsafe(),
         k.getUnsafe(),
      ).valid()
   }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   e: Validated<ERROR, E>,
   f: Validated<ERROR, F>,
   g: Validated<ERROR, G>,
   h: Validated<ERROR, H>,
   i: Validated<ERROR, I>,
   j: Validated<ERROR, J>,
   k: Validated<ERROR, K>,
   l: Validated<ERROR, L>,
   fn: (A, B, C, D, E, F, G, H, I, J, K, L) -> R
): Validated<ERROR, R> {

   val errors = listOf(a, b, c, d, e, f, g, h, i, j, k, l)
      .filterIsInstance<Validated.Invalid<ERROR>>()
      .flatMap { it.errors }

   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      fn(
         a.getUnsafe(),
         b.getUnsafe(),
         c.getUnsafe(),
         d.getUnsafe(),
         e.getUnsafe(),
         f.getUnsafe(),
         g.getUnsafe(),
         h.getUnsafe(),
         i.getUnsafe(),
         j.getUnsafe(),
         k.getUnsafe(),
         l.getUnsafe(),
      ).valid()
   }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   e: Validated<ERROR, E>,
   f: Validated<ERROR, F>,
   g: Validated<ERROR, G>,
   h: Validated<ERROR, H>,
   i: Validated<ERROR, I>,
   j: Validated<ERROR, J>,
   k: Validated<ERROR, K>,
   l: Validated<ERROR, L>,
   m: Validated<ERROR, M>,
   fn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> R
): Validated<ERROR, R> {

   val errors = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m)
      .filterIsInstance<Validated.Invalid<ERROR>>()
      .flatMap { it.errors }

   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      fn(
         a.getUnsafe(),
         b.getUnsafe(),
         c.getUnsafe(),
         d.getUnsafe(),
         e.getUnsafe(),
         f.getUnsafe(),
         g.getUnsafe(),
         h.getUnsafe(),
         i.getUnsafe(),
         j.getUnsafe(),
         k.getUnsafe(),
         l.getUnsafe(),
         m.getUnsafe(),
      ).valid()
   }
}


fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, R, ERROR> mapN(
   a: Validated<ERROR, A>,
   b: Validated<ERROR, B>,
   c: Validated<ERROR, C>,
   d: Validated<ERROR, D>,
   e: Validated<ERROR, E>,
   f: Validated<ERROR, F>,
   g: Validated<ERROR, G>,
   h: Validated<ERROR, H>,
   i: Validated<ERROR, I>,
   j: Validated<ERROR, J>,
   k: Validated<ERROR, K>,
   l: Validated<ERROR, L>,
   m: Validated<ERROR, M>,
   n: Validated<ERROR, N>,
   fn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> R
): Validated<ERROR, R> {

   val errors = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
      .filterIsInstance<Validated.Invalid<ERROR>>()
      .flatMap { it.errors }

   return if (errors.isNotEmpty()) Validated.Invalid(errors) else {
      fn(
         a.getUnsafe(),
         b.getUnsafe(),
         c.getUnsafe(),
         d.getUnsafe(),
         e.getUnsafe(),
         f.getUnsafe(),
         g.getUnsafe(),
         h.getUnsafe(),
         i.getUnsafe(),
         j.getUnsafe(),
         k.getUnsafe(),
         l.getUnsafe(),
         m.getUnsafe(),
         n.getUnsafe()
      ).valid()
   }
}
