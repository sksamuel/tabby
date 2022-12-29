package com.sksamuel.tabby.ktor

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext

fun main() {
   val server = embeddedServer(Netty, port = 8080) {
      routing {
         endpoints()
      }
   }
   server.start(true)
}

fun Route.endpoints() {
   with(DefaultResponseHandler) {
      get("/health") {
         respondWith(getUser("sam"))
      }
   }
}

fun getUser(name: String): Result<String> {
   return Result.success(name)
}

interface ResponseHandler<T> {
   suspend fun PipelineContext<*, ApplicationCall>.handleResult(result: Result<T>)
}

object DefaultResponseHandler : ResponseHandler<Any> {
   override suspend fun PipelineContext<*, ApplicationCall>.handleResult(result: Result<Any>) {
      // todo log out error here
      result.fold(
         { call.respond(HttpStatusCode.OK) },
         { call.respond(HttpStatusCode.InternalServerError) },
      )
   }
}

context(ResponseHandler<T>)
   suspend fun <T> PipelineContext<*, ApplicationCall>.respondWith(result: Result<T>) = handleResult(result)
