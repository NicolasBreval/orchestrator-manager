package org.nitb.orchestrator2.model

import com.fasterxml.jackson.annotation.JsonFormat
import org.nitb.orchestrator2.db.entity.TaskHistorical
import org.nitb.orchestrator2.db.entity.TaskOperation
import org.nitb.orchestrator2.db.entity.TaskType
import java.time.LocalDateTime

data class Task(
    var name: String = "",
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var creationDate: LocalDateTime = LocalDateTime.now(),
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var lastModification: LocalDateTime = LocalDateTime.now(),
    var active: Boolean,
    var stopped: Boolean,
    var type: TaskType,
    var parameters: String
) {
    constructor(taskHistorical: TaskHistorical): this(taskHistorical.task!!.name, taskHistorical.task!!.creationDate,
        taskHistorical.task!!.lastModification, taskHistorical.task!!.active,
        taskHistorical.task!!.stopped, taskHistorical.task!!.type, taskHistorical.parameters)

    private fun toDbTask(): org.nitb.orchestrator2.db.entity.Task {
        return org.nitb.orchestrator2.db.entity.Task(
            name = this.name,
            creationDate = this.creationDate,
            lastModification = this.lastModification,
            active = this.active,
            stopped = this.stopped,
            type = this.type
        )
    }

    fun toDbTaskHistorical(username: String, operation: TaskOperation, task: org.nitb.orchestrator2.db.entity.Task = toDbTask()): TaskHistorical {
        return TaskHistorical(
            username = username,
            parameters = this.parameters,
            operation = operation,
            task = task
        )
    }
}