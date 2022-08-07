package org.nitb.orchestrator2.controller.exception

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import org.nitb.orchestrator2.model.ControllerExceptionMessage

@Produces
@Singleton
@Requires(classes = [Exception::class, ExceptionHandler::class])
class ExceptionHandler:
    ExceptionHandler<Exception, HttpResponse<*>> {
    override fun handle(request: HttpRequest<*>?, exception: Exception?): HttpResponse<*> {
        return HttpResponse.ok(ControllerExceptionMessage(exception?.message ?: "", exception?.stackTrace ?: arrayOf()))
    }
}