package org.nitb.orchestrator2.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

/**
 * Simple controller used to check if server is accessible. This method is normally used by health systems of
 * several http-based solutions, like Consul or Kubernetes, to check if any service is available.
 */
@Controller("/health")
class HealthController {

    /**
     * Endpoint used to check is service is available.
     *
     * @return A [String] with 'OK'.
     */
    @Get
    fun health(): HttpResponse<*> {
        return HttpResponse.ok("OK")
    }

}