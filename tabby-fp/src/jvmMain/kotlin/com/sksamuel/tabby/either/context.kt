package com.sksamuel.tabby.either

fun main() {
//   val users = loadUsers().bind()
}

sealed interface Errors {
   data object UserLoadFailure : Errors
}

data class User(val name: String)
data class Location(val name: String)

fun loadUsers(): Either<Errors, User> {
   TODO()
}

fun loadLocation(): Either<Error, Location> {
   TODO()
}

interface ResultScope<A> {
   fun shift(error: A): Nothing
}

context(ResultScope<A>)
fun <A, B> Either<A, B>.bind(): B = when (this) {
   is Either.Right -> this.b
   is Either.Left -> shift(this.a)
}




