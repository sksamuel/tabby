package com.sksamuel.tabby.validation

import kotlin.jvm.JvmName

fun interface Parser<I, A, out E> {

   companion object {

      operator fun <I> invoke(): Parser<I, I, Nothing> = parser { it.valid() }

      /**
       * Creates a new [Parser] that rejects nulls, from the provided non-nullable validating function.
       */
      fun <I, A, E> nullable(f: (I) -> Validated<E, A>): Parser<I?, A?, E> = parser(f).nullable()
   }

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
 * Modifies an existing String -> String [Parser] to reject blank string inputs.
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
 * Widens an existing [Parser] to accept null inputs which are rejected using the given [ifNull] function.
 */
fun <I, A, E> Parser<I, A, E>.notNull(ifNull: () -> E): Parser<I?, A, E> {
   return Parser { input ->
      when (input) {
         null -> ifNull().invalid()
         else -> this@notNull.parse(input)
      }
   }
}

/**
 * Widens an existing [Parser] to accept null inputs which are returned as valid.
 */
fun <I, A, E> Parser<I, A, E>.nullable(): Parser<I?, A?, E> {
   return Parser { input ->
      if (input == null) Validated.Valid(null) else this@nullable.parse(input)
   }
}

fun <I, E> Parser<I, String, E>.minlen(len: Int, ifError: (String) -> E): Parser<I, String, E> {
   return Parser { input ->
      this@minlen.parse(input)
         .flatMap { if (it.length < len) ifError(it).invalid() else it.valid() }
   }
}

@JvmName("minlenNullable")
fun <I, E> Parser<I, String?, E>.minlen(len: Int, ifError: (String?) -> E): Parser<I, String?, E> {
   return Parser { input ->
      this@minlen.parse(input)
         .flatMap { if (it == null) Validated(null) else if (it.length < len) ifError(it).invalid() else it.valid() }
   }
}

/**
 * Chains a [Parser] to convert String -> Int
 */
fun <E> Parser<String, String, E>.int(ifError: (String) -> E): Parser<String, Int, E> = parser {
   when (val int = it.toIntOrNull()) {
      null -> ifError(it).invalid()
      else -> int.valid()
   }
}

/**
 * Returns a [Parser] that modifies an existing parser by trimming string input prior to
 * invoking the parser.
 */
fun <A, E> Parser<String, A, E>.trim(): Parser<String, A, E> = this.contramap { it.trim() }

/**
 * Returns a [Parser] that modifies an existing parser by trimming string input prior to
 * invoking the parser.
 */
@JvmName("trimN")
fun <A, E> Parser<String?, A, E>.contratrim(): Parser<String?, A, E> = this.contramap { it?.trim() }

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
 * Returns a [Parser] that adds additional validation to the result of a previous parser.
 */
fun <I, A, B, E> Parser<I, A, E>.validate(f: (A) -> Validated<E, B>): Parser<I, B, E> =
   parser { input -> this@validate.parse(input).flatMap { f(it) } }

/**
 * Returns a [Parser] that rejects blank or null inputs.
 */
fun <A, E> Parser<String, A, E>.notNullOrBlank(ifNullOrBlank: () -> E): Parser<String?, A, E> {
   return parser { input ->
      when {
         input.isNullOrBlank() -> ifNullOrBlank().invalid()
         else -> this@notNullOrBlank.parse(input)
      }
   }
}

/**
 * Returns a method reference from I to Validated<E,A> as a [Parser].
 */
fun <I, A, E> (kotlin.reflect.KFunction1<I, Validated<E, A>>).asParser(): Parser<I, A, E> {
   val self = this@asParser
   return parser { self.invoke(it) }
}

/**
 * Creates a new [Parser] from J -> A by mapping the input to I and then feeding into
 * the receiver parser.
 */
fun <I, J, A, E> Parser<I, A, E>.contramap(f: (J) -> I): Parser<J, A, E> =
   parser { this@contramap.parse(f(it)) }
