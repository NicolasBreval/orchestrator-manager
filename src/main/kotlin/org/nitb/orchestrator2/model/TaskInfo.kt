package org.nitb.orchestrator2.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.micronaut.core.annotation.Introspected

@Introspected
data class TaskInfo (
    val type: String,
    val name: String,
    val parameters: Map<String, Any?>,
    @JsonIgnore
    @Transient
    val strParameters: String
)