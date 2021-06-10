package com.sksamuel.tabby.validation

fun interface Parser<I, A, E> {
   companion object {}

   fun parse(input: I): Validated<E, A>
}

/**
 * Returns a [Parser] backed by the given function.
 */
fun <I, A, E> parser(fn: (I) -> Validated<E, A>) = Parser<I, A, E> { input -> fn(input) }


/**
 * Wraps an existing parser to accept a collection of inputs of the underlying input type I,
 * returning a collection of outputs of the underlying output type A.
 */
fun <I, A, E> Parser<I, A, E>.repeated(): Parser<List<I>, List<A>, E> {
   return parser { input ->
      input.map { this@repeated.parse(it) }.traverse()
   }
}


/**
 * Modifies an existing string parser to reject blank string inputs.
 */
fun <A, E> Parser<String, A, E>.notBlank(ifBlank: () -> E): Parser<String, A, E> {
   return Parser { input ->
      when {
         input.isBlank() -> ifBlank().invalid()
         else -> this@notBlank.parse(input)
      }
   }
}

/**
 * Returns a new [Parser] from the given parser that rejects null inputs.
 */
fun <I, A, E> Parser<I, A, E>.notnull(ifNull: () -> E): Parser<I?, A, E> {
   return Parser { input ->
      when (input) {
         null -> ifNull().invalid()
         else -> this@notnull.parse(input)
      }
   }
}

/**
 * Returns a new [Parser] from the given parser that accepts null inputs.
 */
fun <I, A, E> Parser<I, A, E>.nullable(): Parser<I?, A?, E> {
   return Parser { input ->
      if (input == null) Validated.Valid(null) else this@nullable.parse(input)
   }
}

/**
 * Returns a [Parser] that modifies an existing parser by trimming string input prior to
 * invoking the parser.
 */
fun <A, E> Parser<String, A, E>.trim(): Parser<String, A, E> = parser { input ->
   this@trim.parse(input.trim())
}

/**
 * Returns a [Parser] that maps a result A into a result B.
 */
fun <I, A, B, E> Parser<I, A, E>.map(f: (A) -> B): Parser<I, B, E> = parser { this@map.parse(it).map(f) }

/**
 * Returns a [Parser] that maps a non-nullable value A into a value B.
 * If the parsed value is null, the returned parser also returns null.
 */
fun <I, A, B, E> Parser<I, A?, E>.mapIfNotNull(f: (A) -> B): Parser<I, B?, E> = parser { input ->
   this@mapIfNotNull.parse(input).map { if (it == null) null else f(it) }
}

/**
 * Creates a new parser from the given non-nullable validating function that accepts null.
 */
fun <I, A, E> Parser.Companion.nullable(f: (I) -> Validated<E, A>): Parser<I?, A?, E> = parser(f).nullable()

/**
 * Creates a String parser that returns a trimmed string.
 */
fun <E> Parser.Companion.trimmed(): Parser<String, String, E> = parser { it.trim().valid() }

/**
 * Returns a [Parser] that adds additional validation to the result of a previous parser.
 */
fun <I, A, B, E> Parser<I, A, E>.validate(f: (A) -> Validated<E, B>): Parser<I, B, E> =
   parser { input -> this@validate.parse(input).flatMap { f(it) } }

/**
 * Returns a [Parser] that parses an Int from a String.
 */
fun <E> Parser.Companion.int(ifError: (String) -> E): Parser<String, Int, E> = parser {
   when (val int = it.toIntOrNull()) {
      null -> ifError(it).invalid()
      else -> int.valid()
   }
}

/**
 * Returns a [Parser] from a string that requires the string to be not-blank.
 */
fun <E> Parser.Companion.notBlank(ifBlank: (String) -> E): Parser<String, String, E> = parser {
   when {
      it.isBlank() -> ifBlank(it).invalid()
      else -> it.valid()
   }
}

/**
 * Returns a [Parser] from a string that requires the string to have a min length.
 */
fun <E> Parser.Companion.minlen(len: Int, ifError: (String) -> E): Parser<String, String, E> = parser {
   when {
      it.length < len -> ifError(it).invalid()
      else -> it.valid()
   }
}

fun <I, J, A, E> Parser<I, A, E>.contramap(f: (J) -> I): Parser<J, A, E> =
   parser { this@contramap.parse(f(it)) }

/**
 * Returns a [Parser] that parses a Double from a String.
 */
fun <E> Parser.Companion.double(ifError: (String) -> E): Parser<String, Double, E> = parser {
   when (val int = it.toDoubleOrNull()) {
      null -> ifError(it).invalid()
      else -> int.valid()
   }
}
