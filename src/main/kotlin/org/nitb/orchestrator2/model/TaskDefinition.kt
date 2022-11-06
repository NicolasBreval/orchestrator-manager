package org.nitb.orchestrator2.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class TaskDefinition(
        val type: String,
        val name: String,
        val parameters: String,
        val stopped: Boolean
)