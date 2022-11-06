package org.nitb.orchestrator2.controller.exception

import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import org.nitb.orchestrator2.model.ControllerExceptionMessage
import org.slf4j.LoggerFactory

@Produces
@Singleton
@Requires(classes = [Exception::class, ExceptionHandler::class])
@Suppress("UNUSED")
class ExceptionHandler(
    @Value("\${orchestrator.exception.show-trace}")
    val showTrace: Boolean
): ExceptionHandler<Exception, HttpResponse<*>> {
    override fun handle(request: HttpRequest<*>?, exception: Exception?): HttpResponse<*> {
        logger.error(exception?.message, exception)
        return HttpResponse.ok(ControllerExceptionMessage(exception?.message ?: "", if (showTrace) exception?.stackTrace ?: arrayOf() else arrayOf()))
    }

    private val logger = LoggerFactory.getLogger("exception-handler")
}