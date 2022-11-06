package org.nitb.orchestrator2.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class ControllerExceptionMessage (
    var error: String,
    var trace: Array<StackTraceElement> = arrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ControllerExceptionMessage) return false

        if (error != other.error) return false
        if (!trace.contentEquals(other.trace)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = error.hashCode()
        result = 31 * result + trace.contentHashCode()
        return result
    }
}