package com.sksamuel.tabby.validation

/**
 * Validates any object is not null.
 */
fun <E, A> Validated.Companion.notNull(input: A?, ifNull: () -> E): Validated<E, A> {
   return input?.valid() ?: ifNull().invalid()
}

/**
 * Validates that string input is not null and not blank.
 */
fun <E> Validated.Companion.notNullOrBlank(input: String?, ifNullOrBlank: () -> E): Validated<E, String> {
   return if (input.isNullOrBlank()) ifNullOrBlank().invalid() else input.valid()
}

/**
 * Validates that string input is not blank, but null is permitted.
 */
fun <E> Validated.Companion.notBlank(input: String?, ifBlank: () -> E): Validated<E, String?> {
   return when {
      input == null -> Validated(null)
      input.isBlank() -> ifBlank().invalid()
      else -> input.valid()
   }
}
