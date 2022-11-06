package org.nitb.orchestrator2.db.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.nitb.orchestrator2.db.entity.Worker

@Repository
interface WorkerRepository: CrudRepository<Worker, String> {
    fun findByIsConnectedTrue(): Set<Worker>
}