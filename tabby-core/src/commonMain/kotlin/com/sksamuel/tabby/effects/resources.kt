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
      fun <A> just(a: A): Resource<A> = Resource({ a }, {})
      val unit: Resource<Unit> = just(Unit)
   }

   suspend fun useUnit(f: suspend (A) -> Unit) {
      suspend fun use() {
         val a = catch { acquire() }
         a.onSuccess { catch { f(it) } }
         a.onSuccess { catch { release(it) } }
      }
      if (context == null) use() else withContext(context) { use() }
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

   fun context(context: CoroutineContext): Resource<A> = copy(context = context)
}
