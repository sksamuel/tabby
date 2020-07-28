package com.sksamuel.tabby

interface Optional<out A> {
   fun toOption(): Option<A>
}
