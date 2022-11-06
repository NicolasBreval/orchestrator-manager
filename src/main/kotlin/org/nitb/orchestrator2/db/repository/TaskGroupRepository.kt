package org.nitb.orchestrator2.db.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.nitb.orchestrator2.db.entity.TaskGroup
import org.nitb.orchestrator2.db.entity.TaskGroupPk

@Repository
interface TaskGroupRepository: CrudRepository<TaskGroup, TaskGroupPk> {

    fun findByUserGroupAndActiveTrue(userGroup: String): List<TaskGroup>
}