package com.sksamuel.tabby.`try`

data class MonadControlException(val t: Throwable) : Throwable()

@Deprecated("with is a scope function in kotlin; use comprehension {}")
fun <A> with(f: () -> A): Try<A> = comprehension(f)
fun <A> comprehension(f: () -> A): Try<A> {
   return try {
      f().success()
   } catch (t: MonadControlException) {
      t.t.failure()
   } catch (t: Throwable) {
      t.failure()
   }
}
