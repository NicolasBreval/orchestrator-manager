package org.nitb.orchestrator2.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class TaskGroupEntry (
    val name: String,
    val taskList: List<TaskDefinition>
)