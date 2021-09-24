package com.sksamuel.tabby.iterables

fun <A> Iterable<A>.tripartition(
   isFirst: (A) -> Boolean,
   isSecond: (A) -> Boolean,
): Triple<List<A>, List<A>, List<A>> {

   val first = ArrayList<A>()
   val second = ArrayList<A>()
   val third = ArrayList<A>()

   for (element in this) {
      if (isFirst(element)) {
         first.add(element)
      } else if (isSecond(element)) {
         second.add(element)
      } else {
         second.add(element)
      }
   }

   return Triple(first, second, third)
}

fun <A, B> Iterable<A>.tripartition(
   isFirst: (A) -> Boolean,
   isSecond: (A) -> Boolean,
   mapFn: (A) -> B,
): Triple<List<B>, List<B>, List<B>> {

   val first = ArrayList<B>()
   val second = ArrayList<B>()
   val third = ArrayList<B>()

   for (element in this) {
      if (isFirst(element)) {
         first.add(mapFn(element))
      } else if (isSecond(element)) {
         second.add(mapFn(element))
      } else {
         second.add(mapFn(element))
      }
   }

   return Triple(first, second, third)
}
