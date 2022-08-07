package org.nitb.orchestrator2

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.nitb.orchestrator2.manager.TaskManager
import org.nitb.orchestrator2.db.repository.TaskHistoricalRepository
import org.nitb.orchestrator2.db.repository.TaskRepository

@MicronautTest
class DatabaseTest {

    @Test
    fun addNewTaskTest() {

    }

    @Test
    fun test() {

    }


    @Inject
    private lateinit var taskManager: TaskManager

    @Inject
    private lateinit var taskRepository: TaskRepository

    @Inject
    private lateinit var taskHistoricalRepository: TaskHistoricalRepository

}
