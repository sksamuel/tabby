package com.sksamuel.tabby

interface Err

data class ThrowableErr(val throwable: Throwable) : Err
data class StringErr(val msg: String) : Err

@Deprecated("use IO")
sealed class Attempt<out A> : Optional<A> {

   data class Success<T>(val value: T) : Attempt<T>()
   data class Failure(val err: Err) : Attempt<Nothing>()

   companion object {
      inline operator fun <T> invoke(f: () -> T): Attempt<T> = try {
         Success(f())
      } catch (t: Throwable) {
         Failure(ThrowableErr(t))
      }

      fun failed(t: Throwable): Attempt<Nothing> = Failure(ThrowableErr(t))
      fun failed(msg: String): Attempt<Nothing> = Failure(StringErr(msg))
   }

   override fun toOption(): Option<A> = when (this) {
      is Success -> Option.Some(value)
      else -> none
   }

   fun isSuccess(): Boolean = this is Success
   fun isFailure(): Boolean = this is Failure

   inline fun onFailure(f: (Err) -> Unit): Attempt<A> {
      fold({ f(it) }, {})
      return this
   }

   inline fun onSuccess(f: (A) -> Unit): Attempt<A> {
      fold({}, { f(it) })
      return this
   }

   fun orNull(): A? = when (this) {
      is Success -> this.value
      is Failure -> null
   }

   inline fun <B> map(f: (A) -> B): Attempt<B> = fold({ Failure(it) }, { Success(f(it)) })

   inline fun <B> flatMap(f: (A) -> Attempt<B>): Attempt<B> = fold({ Failure(it) }, { f(it) })

   inline fun <U> fold(ifFailure: (Err) -> U, ifSuccess: (A) -> U): U = when (this) {
      is Success -> ifSuccess(this.value)
      is Failure -> ifFailure(err)
   }
}

fun <T> T?.toAttempt(ifNull: () -> Err): Attempt<T> = if (this == null) Attempt.Failure(ifNull()) else Attempt.Success(
   this)

fun <T> Attempt<Attempt<T>>.flatten(): Attempt<T> {
   return when (this) {
      is Attempt.Failure -> Attempt.Failure(this.err)
      is Attempt.Success -> this.value
   }
}

inline fun <A : B, B> Attempt<A>.getOrElse(f: (Err) -> B): B = fold({ f(it) }, { it })
