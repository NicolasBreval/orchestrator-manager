package org.nitb.orchestrator2.controller

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import org.nitb.orchestrator2.model.TaskGroupEntry
import org.nitb.orchestrator2.service.TaskGroupService
import javax.annotation.Nullable

@Secured(SecurityRule.IS_AUTHENTICATED)
@Suppress("UNUSED")
@Controller("/tasks")
class TaskGroupController {

    @Put
    @Secured("OPERATOR", "ADMIN")
    fun insertOrUpdate(@Body group: TaskGroupEntry, authentication: Authentication) {
        taskGroupService.insertOrUpdateGroup(group, authentication.attributes["group"] as String)
    }

    @Get
    @Secured("VIEWER", "OPERATOR", "ADMIN")
    fun fetch(@QueryValue name: String, authentication: Authentication): TaskGroupEntry {
        return taskGroupService.fetchGroup(name, authentication.attributes["group"] as String)
    }

    @Get("/all")
    @Secured("VIEWER", "OPERATOR", "ADMIN")
    fun fetchMany(authentication: Authentication): List<TaskGroupEntry> {
        return taskGroupService.fetchAllGroups(authentication.attributes["group"] as String)
    }

    @Delete
    @Secured("OPERATOR", "ADMIN")
    fun delete(@QueryValue @Nullable group: String? = null, @QueryValue @Nullable tasks: List<String>?, authentication: Authentication) {
        taskGroupService.delete(group, tasks ?: listOf(), authentication.attributes["group"] as String)
    }

    @Inject
    private lateinit var taskGroupService: TaskGroupService

}