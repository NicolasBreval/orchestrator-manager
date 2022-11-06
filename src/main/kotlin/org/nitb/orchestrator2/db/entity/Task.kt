package org.nitb.orchestrator2.db.entity

import io.micronaut.core.annotation.Introspected
import org.hibernate.Hibernate
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.*

@Introspected
@Entity
@Table(name = "tasks")
@IdClass(TaskPk::class)
data class Task(
        @Id
        val name: String,
        val type: String,
        @Id
        val userGroup: String,
        val creationDate: ZonedDateTime = ZonedDateTime.now(),
        @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        @JoinColumn(name = "worker")
        var worker: Worker? = null,
        @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        @JoinTable(name = "task_like", joinColumns = [JoinColumn(name = "task_name"), JoinColumn(name = "task_user")],
                inverseJoinColumns = [JoinColumn(name = "group_name"), JoinColumn(name = "group_user")])
        val groups: MutableSet<TaskGroup> = mutableSetOf(),
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
                other as Task

                return name == other.name
                        && userGroup == other.userGroup
        }

        override fun hashCode(): Int = Objects.hash(name, userGroup)

        @Override
        override fun toString(): String {
                return this::class.simpleName + "(name = $name , userGroup = $userGroup , type = $type , creationDate = $creationDate , worker = $worker )"
        }
}