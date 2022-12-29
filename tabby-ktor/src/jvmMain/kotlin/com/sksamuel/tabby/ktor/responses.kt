package com.sksamuel.tabby.ktor

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext

fun main() {
   val server = embeddedServer(Netty, port = 8080) {
      routing {
         get("/health") {
            with(DefaultErrorHandler) {
               respond(getUser("sam"))
            }
         }
      }
   }
   server.start(true)
}

fun getUser(name: String): Result<String> {
   return Result.success(name)
}

interface ErrorHandler {
   suspend fun PipelineContext<*, ApplicationCall>.handleError(t: Throwable)
}

object DefaultErrorHandler : ErrorHandler {
   override suspend fun PipelineContext<*, ApplicationCall>.handleError(t: Throwable) {
      // todo log out error here
      call.respond(HttpStatusCode.InternalServerError)
   }
}

context(ErrorHandler)
   suspend fun <T> PipelineContext<*, ApplicationCall>.respond(result: Result<T>) {
   result.fold(
      { call.respond(HttpStatusCode.OK) },
      { handleError(it) },
   )
}
