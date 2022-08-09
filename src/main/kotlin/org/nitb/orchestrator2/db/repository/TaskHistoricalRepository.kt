package org.nitb.orchestrator2.db.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.nitb.orchestrator2.db.entity.TaskHistorical
import java.math.BigDecimal
import java.util.*

/**
 * Repository object used to retrieve and store information about tasks historical.
 *
 * @see TaskHistorical
 */
@Repository
interface TaskHistoricalRepository: CrudRepository<TaskHistorical, BigDecimal> {

    /**
     * Obtains last historical referenced to a task with name [name].
     *
     * @param name
     *
     * @return An [Optional] object, if task exists returns an [Optional] with the task inside, else an empty [Optional].
     */
    @Query("from task_historicals th where th.task.name = :name and th.id = (select max(th2.id) from task_historicals th2 where th2.task.name = :name)")
    fun findLastHistoricalByTaskName(name: String): Optional<TaskHistorical>

    /**
     * Obtains last historical referenced to all tasks with their names in [names].
     *
     * @param names List of names of tasks to retrieve.
     *
     * @return List of tasks with their last historical filtered by [names].
     */
    @Query("from task_historicals th where th.id in (select max(th2.id) from task_historicals th2 where th2.task.name in :names)")
    fun findLastHistoricalByTaskNames(names: List<String>): List<TaskHistorical>

    /**
     * Obtains last historical for all tasks registered on database.
     *
     * @return List with all tasks with their last historical.
     */
    @Query("from task_historicals th where th.id in (select max(th2.id) from task_historicals th2 where th.task.active = true group by th2.task)")
    fun findLastHistoricalByTask(): List<TaskHistorical>
}