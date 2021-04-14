package com.sksamuel.tabby.effects

import com.sksamuel.tabby.`try`.Try
import com.sksamuel.tabby.`try`.catch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

data class Resource<A>(
   val acquire: suspend () -> A,
   val release: suspend (A) -> Unit,
   val context: CoroutineContext? = null,
) {

   companion object {
      fun <A> just(a: A) = Resource({ a }, {})
   }

   suspend fun <T> use(f: suspend (A) -> Try<T>): Try<T> {
      suspend fun use(): Try<T> {
         val a = catch { acquire() }
         val t = a.flatMap { f(it) }
         a.onSuccess { catch { release(it) } }
         return t
      }
      return if (context == null) use() else withContext(context) { use() }
   }

   suspend fun <T> useSafe(f: suspend (A) -> T): Try<T> = use { catch { f(it) } }

   fun context(context: CoroutineContext): Resource<A> = copy(context = context)
}
