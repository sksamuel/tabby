package com.sksamuel.tabby.validation

fun interface Parser<in I, out A, out E> {

   companion object {
      /**
       * Create a new [Parser] for a base input type I.
       */
      operator fun <I> invoke(): Parser<I, I, Nothing> = parser { it.valid() }
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
 * Widens an existing String -> String [Parser] to reject blank string inputs.
 */
fun <I, A : String?, E> Parser<I, A, E>.notBlank(ifBlank: () -> E): Parser<I, A, E> {
   return flatMap {
      if (it == null) Validated(null) else if (it.isBlank()) ifBlank().invalid() else it.valid()
   } as Parser<I, A, E>
}

/**
 * Modifies an existing [Parser] to reject nulls.
 */
fun <I, A, E> Parser<I, A?, E>.notNull(ifNull: () -> E): Parser<I, A, E> {
   return flatMap {
      when (it) {
         null -> ifNull().invalid()
         else -> it.valid()
      }
   }
}

/**
 * Wraps an existing [Parser] String? -> String? to reject nulls and blank strings.
 */
fun <E> Parser<String?, String?, E>.notNullOrBlank(ifNullOrBlank: () -> E): Parser<String?, String, E> {
   return flatMap { if (it.isNullOrBlank()) ifNullOrBlank().invalid() else it.valid() }
}

/**
 * Widens an existing [Parser] to accept null inputs which are returned as valid.
 */
fun <I, A, E> Parser<I, A, E>.nullable(): Parser<I?, A?, E> {
   return Parser { input ->
      if (input == null) Validated.Valid(null) else this@nullable.parse(input)
   }
}

/**
 * Wraps a [Parser] String -> String by enforcing a min length.
 */
fun <I, A : String?, E> Parser<I, A, E>.minlen(len: Int, ifError: (String) -> E): Parser<I, A, E> =
   flatMap {
      when {
         it == null -> Validated(null) as Validated<E, A>
         it.length < len -> ifError(it).invalid()
         else -> it.valid()
      }
   }

/**
 * Extends a [Parser] which has a nullable result type to return non-null result types.
 *
 * For any input values which are null, the given function [ifNull] is invoked and the
 * output of that function is used as the return value from the parser.
 */
fun <I, A, E> Parser<I, A?, E>.default(ifNull: () -> A): Parser<I, A, E> {
   return parser { input ->
      this@default.parse(input).flatMap {
         it?.valid() ?: ifNull().valid()
      }
   }
}

/**
 * Chains a [Parser] to convert String? -> Int
 */
fun <I, A : String?, E> Parser<I, A, E>.int(ifError: (A) -> E): Parser<I, Int, E> =
   flatMap {
      val int = it?.toIntOrNull()
      int?.valid() ?: ifError(it).invalid()
   }

/**
 * Returns a [Parser] that modifies an existing parser by trimming string input.
 */
fun <I, A : String?, E> Parser<I, A, E>.trim(): Parser<I, A, E> =
   map { if (it == null) null as A else it.trim() as A }

/**
 * Returns a [Parser] that maps a result A into a result B.
 */
fun <I, A, B, E> Parser<I, A, E>.map(f: (A) -> B): Parser<I, B, E> = parser { this@map.parse(it).map(f) }

/**
 * Returns a [Parser] that maps a result A into a result B.
 */
fun <I, A, B, E> Parser<I, A, E>.flatMap(f: (A) -> Validated<E, B>): Parser<I, B, E> =
   parser { this@flatMap.parse(it).flatMap(f) }

/**
 * Returns a [Parser] that maps a non-nullable value A into a value B.
 * If the parsed value is null, the returned parser also returns null.
 */
fun <I, A, B, E> Parser<I, A?, E>.mapIfNotNull(f: (A) -> B): Parser<I, B?, E> = parser { input ->
   this@mapIfNotNull.parse(input).map { if (it == null) null else f(it) }
}

/**
 * Returns a [Parser] that maps a nullable value into a value A.
 * If the parsed value is not null, then the not null value is returned
 */
fun <I, A, E> Parser<I, A?, E>.mapIfNull(f: () -> A): Parser<I, A, E> = parser { input ->
   this@mapIfNull.parse(input).map { it ?: f() }
}

/**
 * Returns a [Parser] that adds additional validation to the result of a previous parser.
 */
fun <I, A, B, E> Parser<I, A, E>.validate(f: (A) -> Validated<E, B>): Parser<I, B, E> =
   parser { input -> this@validate.parse(input).flatMap { f(it) } }

fun <I, A, E> Parser<I, A, E>.filter(p: (A) -> Boolean, ifFalse: (A) -> E): Parser<I, A, E> {
   return flatMap { if (p(it)) it.valid() else ifFalse(it).invalid() }
}

/**
 * Returns a method reference from I to Validated<E,A> as a [Parser].
 */
fun <I, A, E> (kotlin.reflect.KFunction1<I, Validated<E, A>>).asParser(): Parser<I, A, E> {
   val self = this@asParser
   return parser { self.invoke(it) }
}

/**
 * Creates a new [Parser] from J -> A by transforming an input J into a value I and then
 * feeding that I into the given parser of I -> A.
 */
fun <I, J, A, E> Parser<I, A, E>.contramap(f: (J) -> I): Parser<J, A, E> =
   parser { this@contramap.parse(f(it)) }

