package org.nitb.orchestrator2.service

import grpc.health.v1.HealthService
import grpc.health.v1.HealthService.HealthCheckRequest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.nitb.orchestrator2.db.entity.Worker
import org.nitb.orchestrator2.db.repository.TaskRepository
import org.nitb.orchestrator2.db.repository.WorkerRepository
import org.nitb.orchestrator2.grpc.client.HealthServiceClientGenerator
import org.nitb.orchestrator2.grpc.client.TaskServiceClientGenerator
import org.nitb.orchestrator2.model.TaskDefinition
import org.nitb.orchestrator2.model.WorkerInfo
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

@Singleton
class WorkerService {

    fun registerWorker(workerInfo: WorkerInfo) =
        workerRepository.findById(workerInfo.name).ifPresentOrElse({ found ->
            logger.debug("Received worker information belongs to existent worker '${found.name}'. Updating information...")
            found.serverName = workerInfo.serverName
            found.serverPort = workerInfo.serverPort
            found.isConnected = true
            found.cpuNumber = workerInfo.cpuNumber
            found.availableMemory = workerInfo.availableMemory
            found.serverCpuUsage = workerInfo.serverCpuUsage
            found.systemCpuUsage = workerInfo.systemCpuUsage
            found.osArch = workerInfo.osArch
            found.osVersion = workerInfo.osVersion
            found.workerVersion = workerInfo.workerVersion
            found.buildRevision = workerInfo.buildRevision
            found.lastEvent = ZonedDateTime.now()
            workerRepository.update(found)
        }) {
            logger.debug("New not-previously-registered worker received. Inserting new information...")
            val newWorker = Worker(
                workerInfo.name, workerInfo.serverName, workerInfo.serverPort, true, workerInfo.cpuNumber,
                workerInfo.availableMemory, workerInfo.serverCpuUsage, workerInfo.systemCpuUsage, workerInfo.osArch,
                workerInfo.osVersion, workerInfo.workerVersion, workerInfo.buildRevision, ZonedDateTime.now()
            )
            workerRepository.save(newWorker)
        }

    fun sendTasksToWorkers(tasks: List<TaskDefinition>) =
        getConnectedWorkers().let { workers ->
            logger.debug("Current connected workers: ${workers.toTypedArray()}")
            logger.debug("Grouping tasks with workers using selector algorithm...")
            workerSelectorAlgorithm(workers, tasks).flatMap { (targetWorker, tasks) ->
                logger.debug("Tasks ${tasks.toTypedArray()} will be run in '${targetWorker.name}'")
                workers.map { worker ->
                    logger.debug("Sending tasks to worker '${worker.name}'")
                    Pair(worker, taskServiceClientGenerator.newSyncClient(worker.serverName, worker.serverPort)
                        .addTasks(
                            TaskDefinitionList.newBuilder()
                                .addAllTaskList(tasks.map { task ->
                                    Task.newBuilder().setName(task.name)
                                        .setType(task.type)
                                        .setParameters(task.parameters)
                                        .build()
                                })
                                .setTargetWorker(targetWorker.name)
                                .build()
                        ))
                }
            }.groupBy { it.first }.mapValues { it.value.map { e -> e.second } }
        }

    fun listWorkers() = getConnectedWorkers()

    fun removeTasksInWorkers(tasks: List<String>) =
        getConnectedWorkers().forEach { worker ->
            taskServiceClientGenerator.newSyncClient(worker.serverName, worker.serverPort).removeTasks(TaskServiceNameList.newBuilder().addAllNames(tasks).build())
        }

    private fun getConnectedWorkers() =
        workerRepository.findByIsConnectedTrue().filter { worker ->
            healthServiceClientGenerator.newSyncClient(worker.serverName, worker.serverPort)
                .check(HealthCheckRequest.newBuilder().build()).status == HealthService.HealthCheckResponse.ServingStatus.SERVING
        }

    private fun workerSelectorAlgorithm(
        workers: List<Worker>,
        tasks: List<TaskDefinition>
    ): Map<Worker, List<TaskDefinition>> {
        val sortedWorkers = workers.sortedWith(
            compareByDescending<Worker> { worker -> taskRepository.countNameByWorker(worker) }
                .thenBy { worker -> worker.availableMemory }
                .thenByDescending { worker -> worker.serverCpuUsage }
                .thenByDescending { worker -> worker.systemCpuUsage })

        return mutableMapOf<Worker, MutableList<TaskDefinition>>().let { groups ->
            tasks.forEachIndexed { index, taskHistorical ->
                groups.computeIfAbsent(sortedWorkers[index % sortedWorkers.size]) { mutableListOf() }
                    .add(taskHistorical)
            }
            groups
        }
    }

    @Inject
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Inject
    private lateinit var workerRepository: WorkerRepository

    @Inject
    private lateinit var taskRepository: TaskRepository

    @Inject
    private lateinit var taskServiceClientGenerator: TaskServiceClientGenerator

    @Inject
    private lateinit var healthServiceClientGenerator: HealthServiceClientGenerator
}