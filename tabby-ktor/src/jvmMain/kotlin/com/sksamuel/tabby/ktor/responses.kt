package com.sksamuel.tabby.ktor

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
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
      get("/bar") {
         respondWith(getUser("sam"))
      }
      post("/foo") {
         with(MaxProfilesValidator) {
            withRequest {
               respondWith(getUser("sam"))
            }
         }
      }
   }
}

fun getUser(name: String): Result<String> {
   return Result.success(name)
}

interface ResponseHandler<T> {
   suspend fun PipelineContext<*, ApplicationCall>.handleResult(result: Result<T>)
}

interface RequestValidator<T> {
   fun validate(t: T): Boolean
}

data class GetInternalV1ProfileStatusRequest(
   val profileIds: Set<Long>,
)

context(RequestValidator<T>)
   suspend inline fun <reified T : Any> PipelineContext<*, ApplicationCall>.withRequest(f: (T) -> Unit) {
   val t = call.receive<T>()
   if (validate(t)) f(t) else call.respond(HttpStatusCode.BadRequest)
}

object MaxProfilesValidator : RequestValidator<GetInternalV1ProfileStatusRequest> {
   override fun validate(t: GetInternalV1ProfileStatusRequest): Boolean {
      return t.profileIds.size <= 100
   }
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
