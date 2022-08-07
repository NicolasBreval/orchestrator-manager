package org.nitb.orchestrator2.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Patch
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import jakarta.inject.Inject
import org.nitb.orchestrator2.manager.TaskManager
import org.nitb.orchestrator2.model.Task
import org.slf4j.LoggerFactory

/**
 * Controller used for tasks management.
 */
@Controller("/tasks")
class TasksController {

    @Put
    fun addTasks(@Body task: List<Task>, @Header(defaultValue = "system") user: String): HttpResponse<Void> {
        taskManager.addTasks(task, user)
        return HttpResponse.ok()
    }

    @Delete
    fun deleteTask(@QueryValue names: List<String>, @Header(defaultValue = "system") user: String): HttpResponse<Void> {
        taskManager.removeTasksByName(names, user)
        return HttpResponse.ok()
    }

    @Patch("/stop")
    fun stopTask(@QueryValue names: List<String>, @Header(defaultValue = "system") user: String): HttpResponse<Void> {
        taskManager.stopTasksByName(names, user)
        return HttpResponse.ok()
    }

    @Patch("/start")
    fun startTask(@QueryValue names: List<String>, @Header(defaultValue = "system") user: String): HttpResponse<Void> {
        taskManager.startTaskByName(names, user)
        return HttpResponse.ok()
    }

    @Post
    fun modifyTaskParameters(@QueryValue names: List<String>, @Header(defaultValue = "system") user: String, @Body parameters: Map<String, Map<String, Any>>): HttpResponse<Void> {
        taskManager.modifyTaskParamsByName(names, user, parameters)
        return HttpResponse.ok()
    }

    @Get
    fun getTask(@QueryValue name: String): Task {
        return taskManager.getTask(name)
    }

    @Get("/all")
    fun listTasks(): List<Task> {
        return taskManager.getAllTasks()
    }

    @Inject
    private lateinit var taskManager: TaskManager

    private val logger = LoggerFactory.getLogger(this::class.java)
}