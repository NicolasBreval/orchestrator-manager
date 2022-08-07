package org.nitb.orchestrator2.db.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.nitb.orchestrator2.db.entity.Task
import java.math.BigDecimal
import java.util.*

/**
 * Repository object used to retrieve and store information about tasks.
 *
 * @see Task
 */
@Repository
interface TaskRepository: CrudRepository<Task, BigDecimal> {

    /**
     * Obtains a task by their name.
     *
     * @return An [Optional] object with task requested inside. If no task exists with requested name, returns an empty [Optional].
     */
    fun findByName(name: String): Optional<Task>
}