package com.sksamuel.tabby.validation

fun <E, A> Validated.Companion.notNull(input: A?, ifNull: () -> E): Validated<E, A> {
   return input?.valid() ?: ifNull().invalid()
}

fun <E> Validated.Companion.notBlank(input: String?, ifNull: () -> E): Validated<E, String> {
   return if (input.isNullOrBlank()) ifNull().invalid() else input.valid()
}
