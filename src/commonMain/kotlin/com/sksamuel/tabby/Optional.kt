package com.sksamuel.tabby

import com.sksamuel.tabby.option.Option

interface Optional<out A> {
   fun toOption(): Option<A>
}
