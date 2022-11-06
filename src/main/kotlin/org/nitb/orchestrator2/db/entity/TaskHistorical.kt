package org.nitb.orchestrator2.db.entity

import io.micronaut.core.annotation.Introspected
import org.hibernate.Hibernate
import java.math.BigDecimal
import java.time.ZonedDateTime
import javax.persistence.*

@Introspected
@Entity
@Table(name = "task_historicals")
data class TaskHistorical (
        @Lob
        val parameters: ByteArray,
        val isOrphan: Boolean,
        var active: Boolean,
        var stopped: Boolean,
        val modificationDate: ZonedDateTime = ZonedDateTime.now(),
        @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        @JoinColumn(name = "task_name")
        @JoinColumn(name = "task_user")
        val task: Task? = null,
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: BigDecimal? = null,
) {
    fun touch(parameters: ByteArray = this.parameters, isOrphan: Boolean = this.isOrphan, active: Boolean = this.active, stopped: Boolean = this.stopped): TaskHistorical =
            TaskHistorical(parameters, isOrphan, active, stopped, ZonedDateTime.now(), task)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TaskHistorical

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , parameters = $parameters , isOrphan = $isOrphan , active = $active , stopped = $stopped , modificationDate = $modificationDate , task = $task )"
    }

}