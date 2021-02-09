package com.sksamuel.tabby.`try`

data class MonadControlException(val t: Throwable) : Throwable()

fun <A> forcomp(f: () -> A): Try<A> {
   return try {
      f().success()
   } catch (t: MonadControlException) {
      t.t.failure()
   } catch (t: Throwable) {
      t.failure()
   }
}
