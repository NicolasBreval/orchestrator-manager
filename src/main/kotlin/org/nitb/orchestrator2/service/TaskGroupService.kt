package org.nitb.orchestrator2.service

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.nitb.orchestrator2.db.entity.*
import org.nitb.orchestrator2.db.entity.Task
import org.nitb.orchestrator2.db.repository.TaskGroupRepository
import org.nitb.orchestrator2.db.repository.TaskHistoricalRepository
import org.nitb.orchestrator2.db.repository.TaskRepository
import org.nitb.orchestrator2.exception.DeleteTasksInvalidParametersException
import org.nitb.orchestrator2.exception.GroupNotFoundException
import org.nitb.orchestrator2.exception.NotAvailableWorkersException
import org.nitb.orchestrator2.exception.UnableToUploadTasksInWorkersException
import org.nitb.orchestrator2.model.TaskDefinition
import org.nitb.orchestrator2.model.TaskGroupEntry
import org.slf4j.LoggerFactory
import javax.transaction.Transactional

@Singleton
open class TaskGroupService {

    @Transactional
    open fun insertOrUpdateGroup(group: TaskGroupEntry, userGroup: String) {
        val availableWorkers = workerService.listWorkers()

        if (availableWorkers.isEmpty())
            throw NotAvailableWorkersException("There are no workers to send tasks right now, please try it later")

        logger.debug("Inserting or updating group '${group.name}' with tasks ${group.taskList.map { it.name }.toTypedArray()} for user group '$userGroup'")

        taskGroupRepository.findById(TaskGroupPk(group.name, userGroup)).ifPresentOrElse({ found ->
            logger.debug("Updating group '${found.name}' for user grou '$userGroup'")

            // If already exists, update their information
            // First, check if tasks exists or not
            val storedTasks = taskRepository.findByGroup(found)

            val receivedTaskNames = group.taskList.map { it.name }
            val storedTaskNames = storedTasks.map { it.name }

            val tasksToSave = mutableListOf<Task>()
            val historicalsToSave = mutableListOf<TaskHistorical>()

            // Delete all tasks not listed inside input group
            logger.debug("Checking for task to delete...")
            storedTaskNames
                .filter { !receivedTaskNames.contains(it) }
                .let { tasksToDelete -> storedTasks.filter { tasksToDelete.contains(it.name) } }
                .forEach { task ->
                    logger.debug("Deleting task '${task.name}'")
                    task.groups.removeIf { g -> g.name == group.name && g.userGroup == userGroup }

                    if (task.groups.size > 0) {
                        taskHistoricalRepository.findLastByTask(task).ifPresentOrElse({ historical ->
                            historicalsToSave.add(historical.touch(active = false))
                        }) {
                            logger.warn("There are no historical for a task will be deleted with name `${task.name}`")
                        }
                    }

                    found.tasks.removeIf { t -> t.name == task.name }
                    logger.info("Task '${task.name}' deleted")
                }

            // Add all tasks that not exists in group
            logger.debug("Checking for task to add")
            val userGroupTasks = taskRepository.findByUserGroup(userGroup).groupBy { it.name }
            receivedTaskNames
                .filter { !storedTaskNames.contains(it) }
                .let { tasksToAdd -> group.taskList.filter { tasksToAdd.contains(it.name) } }
                .forEach { task ->
                    logger.info("Inserting task '${task.name}'")
                    val taskToStore = userGroupTasks[task.name]?.get(0) ?: Task(task.name, task.type, userGroup, groups = mutableSetOf(found))
                    tasksToSave.add(taskToStore)
                    historicalsToSave.add(TaskHistorical(task.parameters.toByteArray(), isOrphan = true, active = true, stopped = task.stopped, task = taskToStore))
                    logger.info("Task '${task.name}' has been inserted")
                }

            var errors = false
            val errorMessages = mutableMapOf<String, MutableList<String>>()

            workerService.sendTasksToWorkers(group.taskList).forEach { (worker,  results) ->
                logger.debug("Checking results from worker ${worker.name}")
                results.forEach { result ->
                    if (result.errorsCount > 0) {
                        logger.error("Error uploading tasks ${result.errorsList.joinToString(", ") { it.task }}")
                        errors = true
                        errorMessages.computeIfAbsent(worker.name) { mutableListOf() }.addAll(result.errorsList.map { "${it.error}: ${it.message}" })
                    }
                }
            }

            if (errors) {
                logger.error("Removing all tasks sent to workers...")
                workerService.removeTasksInWorkers(group.taskList.map { it.name })
                throw UnableToUploadTasksInWorkersException()
            }

            logger.debug("Updating all changes in database...")

            taskRepository.saveAll(tasksToSave)
            taskHistoricalRepository.saveAll(historicalsToSave)

            found.tasks.addAll(tasksToSave)

            taskGroupRepository.update(found)

        }, {
            logger.info("Group not exists, inserting before update...")
            val groupToInsert = TaskGroup(group.name, userGroup)
            taskGroupRepository.save(groupToInsert)
            insertOrUpdateGroup(group, userGroup)
        })
    }

    fun fetchGroup(name: String, userGroup: String): TaskGroupEntry {
        return taskGroupRepository.findById(TaskGroupPk(name, userGroup)).map { group ->
            val taskDefinitions = group.tasks.map { task ->
                val historical = taskHistoricalRepository.findLastByTask(task)
                TaskDefinition(task.type, task.name, historical.map { h -> String(h.parameters) }.orElse(""), historical.map { h -> h.stopped }.orElse(true))
            }
            TaskGroupEntry(group.name, taskDefinitions)
        }.orElseThrow { GroupNotFoundException("Not exists any group with name `$name`") }
    }

    fun fetchAllGroups(userGroup: String): List<TaskGroupEntry> {
        return taskGroupRepository.findByUserGroupAndActiveTrue(userGroup).map { group ->
            val taskDefinitions = group.tasks.map { task ->
                val historical = taskHistoricalRepository.findLastByTask(task)
                TaskDefinition(task.type, task.name, historical.map { h -> String(h.parameters) }.orElse(""), historical.map { h -> h.stopped }.orElse(true))
            }
            TaskGroupEntry(group.name, taskDefinitions)
        }
    }

    @Transactional
    open fun delete(group: String? = null, tasks: List<String> = listOf(), user: String) {
        when (group == null) {
            true -> when (tasks.isEmpty()) {
                true -> throw DeleteTasksInvalidParametersException("Invalid input parameters, group or tasks are not specified")
                false -> {
                    val affectedGroups = mutableSetOf<TaskGroup>()
                    val historicals = taskHistoricalRepository.findLastByTaskNamesList(tasks, user).map { historical ->
                        val copy = historical.copy(active = false)
                        affectedGroups.addAll(copy.task!!.groups)
                        copy.task.groups.clear()
                        copy
                    }

                    // DISABLE GROUPS WITHOUT RELATED TASKS
                    affectedGroups.onEach { g -> if (g.tasks.isEmpty()) g.active = false }

                    taskGroupRepository.updateAll(affectedGroups)
                    taskHistoricalRepository.saveAll(historicals)
                }
            }
            false -> when (tasks.isEmpty()) {
                true -> {
                    taskGroupRepository.findById(TaskGroupPk(group, user)).ifPresent { selectedGroup ->
                        val historicals = taskHistoricalRepository.findLastByGroup(selectedGroup)

                        historicals.forEach { historical ->
                            historical.active = false
                            historical.task!!.groups.removeIf { g -> g.name == selectedGroup.name && g.userGroup == selectedGroup.userGroup }
                        }

                        selectedGroup.active = false

                        taskGroupRepository.update(selectedGroup)
                        taskHistoricalRepository.saveAll(historicals)
                    }
                }
                false -> {
                    taskGroupRepository.findById(TaskGroupPk(group, user)).ifPresent { selectedGroup ->
                        val historicals = taskHistoricalRepository.findLastByGroupAndTaskName(selectedGroup, tasks)

                        historicals.forEach { historical ->
                            historical.active = false
                            historical.task!!.groups.removeIf { g -> g.name == selectedGroup.name && g.userGroup == selectedGroup.userGroup }
                        }

                        selectedGroup.active = false

                        taskGroupRepository.update(selectedGroup)
                        taskHistoricalRepository.saveAll(historicals)
                    }
                }
            }
        }
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Inject
    private lateinit var taskGroupRepository: TaskGroupRepository

    @Inject
    private lateinit var taskRepository: TaskRepository

    @Inject
    private lateinit var taskHistoricalRepository: TaskHistoricalRepository

    @Inject
    private lateinit var workerService: WorkerService
}