package org.nitb.orchestrator2.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import org.nitb.orchestrator2.task.parameters.TaskParameters

@Introspected
@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkerInfo(
    val name: String,
    var serverName: String,
    val serverPort: Int,
    val activeTasks: List<TaskInfo> = listOf(),
    val disabledTasks: List<TaskInfo> = listOf(),
    val cpuNumber: Int,
    val availableMemory: Long,
    val serverCpuUsage: Double,
    val systemCpuUsage: Double,
    val osArch: String,
    val osVersion: String,
    val workerVersion: String?,
    val buildRevision: String?
) {

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + serverName.hashCode()
        result = 31 * result + serverPort
        result = 31 * result + activeTasks.hashCode()
        result = 31 * result + disabledTasks.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WorkerInfo) return false

        if (name != other.name) return false
        if (serverName != other.serverName) return false
        if (serverPort != other.serverPort) return false
        if (activeTasks.toSet() != other.activeTasks.toSet()) return false
        if (disabledTasks.toSet() != other.disabledTasks.toSet()) return false

        return true
    }
}