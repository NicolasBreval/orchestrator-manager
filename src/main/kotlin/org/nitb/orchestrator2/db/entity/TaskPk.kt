package org.nitb.orchestrator2.db.entity

import io.micronaut.core.annotation.Introspected
import java.io.Serializable
import javax.persistence.Embeddable

@Introspected
@Embeddable
class TaskPk (
        val name: String,
        val userGroup: String
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TaskGroupPk) return false

        if (name != other.name) return false
        if (userGroup != other.userGroup) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + userGroup.hashCode()
        return result
    }
}