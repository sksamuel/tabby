@file:Suppress("unused")

package com.sksamuel.tabby.tristate

import com.sksamuel.tabby.option.Option
import com.sksamuel.tabby.option.toOption
import com.sksamuel.tabby.tristate.Tristate.None
import com.sksamuel.tabby.tristate.Tristate.Some
import com.sksamuel.tabby.tristate.Tristate.Unspecified

/**
 * Tristate<A> is three-valued variant of [Option] distinguishing between [Some], [None], and [Unspecified].
 */
sealed class Tristate<out A> {

   data class Some<A>(val value: A) : Tristate<A>()
   object None : Tristate<Nothing>()
   object Unspecified : Tristate<Nothing>()

   fun isSome() = this is Some
   fun isNone() = this is None
   fun isUnspecified() = this is Unspecified

   /**
    * Maps the value if a [Some], otherwise performs no op.
    */
   inline fun <B> map(f: (A) -> B): Tristate<B> = when (this) {
      is Some -> Some(f(value))
      None -> None
      Unspecified -> Unspecified
   }

   /**
    * Returns the value if this is a [Some] or null if this is an [None] or [Unspecified].
    */
   fun getValueOrNull(): A? = when (this) {
      is Some -> this.value
      None -> null
      Unspecified -> null
   }

   /**
    * Returns the value if this is a [Some] or throws an exception.
    */
   fun getValueUnsafe(): A = when (this) {
      is Some -> this.value
      else -> throw IllegalStateException("No value")
   }

   fun toOption(): Option<A> = getValueOrNull().toOption()

   fun <B> fold(ifPresent: (A) -> B, ifAbsent: () -> B, ifUnspecified: () -> B): B = when (this) {
      is Some -> ifPresent(this.value)
      None -> ifAbsent()
      Unspecified -> ifUnspecified()
   }
}

/**
 * Returns the value if this is a [Some], otherwise returns the value of [ifElse].
 */
fun <A> Tristate<A>.getValueOrElse(ifElse: () -> A): A = when (this) {
   is Some -> this.value
   else -> ifElse()
}

/**
 * Returns a [Some] if the receiver is not-null, or an [None] otherwise.
 */
fun <T> T?.toTristate(): Tristate<T> = when (this) {
   null -> None
   else -> Some(this)
}
