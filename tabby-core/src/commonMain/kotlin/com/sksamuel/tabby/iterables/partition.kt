package com.sksamuel.tabby.iterables

fun <A> Iterable<A>.tripartition(
   isFirst: (A) -> Boolean,
   isSecond: (A) -> Boolean
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


