package org.nitb.orchestrator2.db.entity

import io.micronaut.core.annotation.Introspected
import org.hibernate.Hibernate
import java.time.ZonedDateTime
import javax.persistence.*

@Introspected
@Entity
@Table(name="workers")
data class Worker (
    @Id
    val name: String,
    var serverName: String,
    var serverPort: Int,
    var isConnected: Boolean?,
    var cpuNumber: Int,
    var availableMemory: Long,
    var serverCpuUsage: Double,
    var systemCpuUsage: Double,
    var osArch: String,
    var osVersion: String,
    var workerVersion: String? = null,
    var buildRevision: String? = null,
    var lastEvent: ZonedDateTime = ZonedDateTime.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Worker

        return name == other.name
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(name = $name , serverName = $serverName , serverPort = $serverPort , lastEvent = $lastEvent , isConnected = $isConnected )"
    }
}