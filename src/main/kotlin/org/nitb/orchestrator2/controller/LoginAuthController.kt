package org.nitb.orchestrator2.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.View

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/login")
@Suppress("UNUSED")
class LoginAuthController {

    @Get("/auth")
    @View("auth")
    fun auth(): Map<String, Any> = mutableMapOf()

    @Get("/authFailed")
    @View("auth")
    fun authFailed(): Map<String, Any> = mapOf("errors" to true)
}