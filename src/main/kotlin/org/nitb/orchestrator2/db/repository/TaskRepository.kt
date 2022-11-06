package org.nitb.orchestrator2.db.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.nitb.orchestrator2.db.entity.Task
import org.nitb.orchestrator2.db.entity.TaskGroup
import org.nitb.orchestrator2.db.entity.TaskPk
import org.nitb.orchestrator2.db.entity.Worker

@Repository
interface TaskRepository: CrudRepository<Task, TaskPk> {

    @Query("select t from Task t join t.groups g where :group = g")
    fun findByGroup(group: TaskGroup): Set<Task>

    fun countNameByWorker(worker: Worker): Long

    fun findByUserGroup(userGroup: String): Set<Task>
}