package org.nitb.orchestrator2.manager

import com.fasterxml.jackson.core.JsonProcessingException
import io.micronaut.jackson.ObjectMapperFactory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.nitb.orchestrator2.db.entity.TaskOperation
import org.nitb.orchestrator2.manager.exception.TaskNotFoundException
import org.nitb.orchestrator2.db.repository.TaskHistoricalRepository
import org.nitb.orchestrator2.db.repository.TaskRepository
import org.nitb.orchestrator2.model.Task
import java.time.LocalDateTime
import javax.persistence.PersistenceException
import javax.transaction.Transactional

/**
 * Manages the information concerning the tasks around the database.
 */
@Singleton
open class TaskManager {

    /**
     * Inserts a list of new tasks in database. If any task already exists, updates their content.
     *
     * @param tasks List of task to be persisted in database.
     * @param user User who makes the operation.
     *
     * @throws PersistenceException If any problem occurs during database operations.
     */
    @Transactional
    @Throws(PersistenceException::class)
    open fun addTasks(tasks: List<Task>, user: String) {
        tasks.forEach { task ->
            upsertTask(task, user, TaskOperation.ADD)
        }
    }

    /**
     * Removes all task in [tasks]. Remember that tasks are never removed, but are marked as inactive on database.
     *
     * @param tasks List of task to be removed.
     * @param user User who makes the operation.
     *
     * @throws PersistenceException If any problem occurs during database operations.
     */
    @Transactional
    @Throws(PersistenceException::class)
    open fun removeTasks(tasks: List<Task>, user: String) {
        tasks.forEach { task ->
            task.active = false
            upsertTask(task, user, TaskOperation.REMOVE)
        }
    }

    /**
     * Disables all task with same name of any in [names]. Remember that tasks are never removed,
     * but are marked as inactive on database.
     *
     * @param names List with names of tasks to remove.
     * @param user User who makes the operation.
     *
     * @throws PersistenceException If any problem occurs during database operations.
     */
    @Transactional
    @Throws(PersistenceException::class)
    open fun removeTasksByName(names: List<String>, user: String) {
        val tasks = getTasks(names)
        removeTasks(tasks, user)
    }

    /**
     * Stops all tasks listed in [tasks]. If a task is stopped, cannot be executed.
     *
     * @param tasks List of task to be stopped.
     * @param user User who makes the operation.
     *
     * @throws PersistenceException If any problem occurs during database operations.
     */
    @Transactional
    @Throws(PersistenceException::class)
    open fun stopTasks(tasks: List<Task>, user: String) {
        tasks.forEach { task ->
            task.stopped = true
            upsertTask(task, user, TaskOperation.STOP)
        }
    }

    /**
     * Stops all tasks with name like any in [names]. If task is stopped, cannot be executed.
     *
     * @param names List with names of tasks to be stopped.
     * @param user User who makes the operation.
     *
     * @throws PersistenceException If any problem occurs during database operations.
     */
    @Throws(TaskNotFoundException::class, PersistenceException::class)
    open fun stopTasksByName(names: List<String>, user: String) {
        val tasks = getTasks(names)
        stopTasks(tasks, user)
    }

    /**
     * Starts all tasks listed in [tasks]. A task only can be executed if is started.
     *
     * @param tasks List of task to be started.
     * @param user User who makes the operation.
     *
     * @throws PersistenceException If any problem occurs during database operations.
     */
    @Transactional
    @Throws(PersistenceException::class)
    open fun startTasks(tasks: List<Task>, user: String) {
        tasks.forEach { task ->
            task.stopped = false
            upsertTask(task, user, TaskOperation.START)
        }
    }

    /**
     * Starts all tasks with name like any in [names]. A task only can be executed if is started.
     *
     * @param names List with names of tasks to be started.
     * @param user User who makes the operation.
     *
     * @throws PersistenceException If any problem occurs during database operations.
     */
    @Throws(PersistenceException::class)
    open fun startTaskByName(names: List<String>, user: String) {
        val tasks = getTasks(names)
        startTasks(tasks, user)
    }

    /**
     * Modifies parameters of all tasks listed in [tasks]. Task parameters provides static information used in
     * their initialization of their executions.
     *
     * @param tasks List of tasks to be updated.
     * @param user User who makes the operation
     * @param parameters Map with task names as key and parameters to be updated as values.
     *
     * @throws PersistenceException If any problem occurs during database operations.
     * @throws JsonProcessingException If any parameter to be updated is not JSON-serializable.
     */
    @Transactional
    @Throws(PersistenceException::class, JsonProcessingException::class)
    open fun modifyTasksParams(tasks: List<Task>, user: String = "system", parameters: Map<String, Map<String, Any>>) {
        tasks.forEach { task ->
            task.parameters = mapper.writeValueAsString(parameters)
            upsertTask(task, user, TaskOperation.MODIFY_CONTENT)
        }
    }

    /**
     * Modifies parameters of all tasks with name in [names]. Task parameters provides static information used in
     * their initialization of their executions.
     *
     * @param names Names of tasks to be updated.
     * @param user User who makes the operation
     * @param parameters Map with task names as key and parameters to be updated as values.
     *
     * @throws PersistenceException If any problem occurs during database operations.
     * @throws JsonProcessingException If any parameter to be updated is not JSON-serializable.
     */
    @Throws(PersistenceException::class, JsonProcessingException::class)
    open fun modifyTaskParamsByName(names: List<String>, user: String, parameters: Map<String, Map<String, Any>>) {
        val tasks = getTasks(names)
        modifyTasksParams(tasks, user, parameters)
    }

    /**
     * Obtains a task from database by their name.
     *
     * @return Task with same name as [name].
     *
     * @throws TaskNotFoundException If no task exists with name requested.
     */
    @Throws(TaskNotFoundException::class)
    open fun getTask(name: String): Task {
        return taskHistoricalRepository.findLastHistoricalByTaskName(name).map {  historical ->
            Task(historical)
        }.orElseThrow { TaskNotFoundException("Doesn't exists any task with name `$name`") }
    }

    /**
     * Obtains all tasks with name in [names].
     *
     * @return List of tasks filtered by their name.
     */
    open fun getTasks(names: List<String>): List<Task> {
        return taskHistoricalRepository.findLastHistoricalByTaskNames(names).map { historical ->
            Task(historical)
        }
    }

    /**
     * Obtains all active tasks registered on database.
     *
     * @return List of active tasks.
     */
    open fun getAllTasks(): List<Task> {
        return taskHistoricalRepository.findLastHistoricalByTask().map {  historical ->
            Task(historical)
        }
    }

    /**
     * Counts number of tasks registered on database, regardless of their status.
     * This method is only used in test classes.
     *
     * @return Number of tasks in database.
     */
    internal open fun countTasks(): Long {
        return taskRepository.count()
    }

    /**
     * Clears all tasks from database. This method is only used in test classes.
     */
    internal open fun cleanTasks() {
        taskHistoricalRepository.deleteAll()
        taskRepository.deleteAll()
    }

    /**
     * Repository instance for tasks management.
     */
    @Inject
    private lateinit var taskRepository: TaskRepository

    /**
     * Repository instance for tasks historical management.
     */
    @Inject
    private lateinit var taskHistoricalRepository: TaskHistoricalRepository

    /**
     * Jackson mapper object used to parse tasks parameters.
     */
    private val mapper = ObjectMapperFactory().objectMapper(null, null)

    /**
     * Inserts or updates a task in database.
     *
     * @param task Task to be inserted or updated.
     * @param user User who makes the operation.
     * @param operation Operation to be registered in task historical.
     *
     * @throws PersistenceException If any problem occurs during database operations.
     */
    @Throws(PersistenceException::class)
    private fun upsertTask(task: Task, user: String, operation: TaskOperation) {
        val optionalPreviousTask = taskRepository.findByName(task.name)

        val historical = if (optionalPreviousTask.isPresent) {
            val previousTask = optionalPreviousTask.get()
            previousTask.active = task.active
            previousTask.stopped = task.stopped
            previousTask.lastModification = LocalDateTime.now()

            val historical = task.toDbTaskHistorical(user, operation, previousTask)
            taskRepository.update(previousTask)
            historical
        } else {
            val historical = task.toDbTaskHistorical(user, operation)
            taskRepository.save(historical.task!!)
            historical
        }

        taskHistoricalRepository.save(historical)
    }

}