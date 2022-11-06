package org.nitb.orchestrator2.db.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.nitb.orchestrator2.db.entity.Task
import org.nitb.orchestrator2.db.entity.TaskGroup
import org.nitb.orchestrator2.db.entity.TaskHistorical
import java.math.BigDecimal
import java.util.*

@Repository
interface TaskHistoricalRepository: CrudRepository<TaskHistorical, BigDecimal> {

    @Query("from TaskHistorical th1 where th1.id = (select max (th.id) from TaskHistorical th join th.task t where t = :task)")
    fun findLastByTask(task: Task): Optional<TaskHistorical>

    @Query("from TaskHistorical th where th.id = (select max(th1.id) from TaskHistorical th1 join th1.task t where t.name in :tasks and t.userGroup = :userGroup group by name, user)")
    fun findLastByTaskNamesList(tasks: List<String>, userGroup: String): List<TaskHistorical>

    @Query("from TaskHistorical th where th.id = (select max(th1.id) from TaskHistorical th1 join th1.task t join t.groups g where g = :group)")
    fun findLastByGroup(group: TaskGroup): List<TaskHistorical>

    @Query("from TaskHistorical th where th.id = (select max(th1.id) from TaskHistorical th1 join th1.task t where t.group = :group and t.name in :tasks)")
    fun findLastByGroupAndTaskName(group: TaskGroup, tasks: List<String>): List<TaskHistorical>
}