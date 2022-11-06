package org.nitb.orchestrator2.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.ServerAuthentication
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.View
import java.security.Principal

@Controller
@Suppress("UNUSED")
class ViewsController {

    @Get
    @View("home")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun home(principal: Principal?): Map<String, Any> {
        val data: MutableMap<String, Any> = mutableMapOf("loggedIn" to (principal != null))
        data["page"] = "home"
        if (principal != null) {
            data["username"] = principal.name

            if (principal is ServerAuthentication) {
                data["role"] = principal.roles.firstOrNull() ?: ""
            }
        }
        return data
    }

    @Get("groups")
    @View("groups")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun tasks(principal: Principal?): Map<String, Any> {
        val data: MutableMap<String, Any> = mutableMapOf("loggedIn" to (principal != null))
        data["page"] = "tasks"
        if (principal != null) {
            data["username"] = principal.name

            if (principal is ServerAuthentication) {
                data["role"] = principal.roles.firstOrNull() ?: ""
            }
        }
        return data
    }
}