package com.sksamuel.tabby.results

import com.sksamuel.tabby.either.Either
import com.sksamuel.tabby.either.left
import com.sksamuel.tabby.either.right

fun <A> Result<A>.toEither(): Either<Throwable, A> = fold({ it.right() }, { it.left() })
