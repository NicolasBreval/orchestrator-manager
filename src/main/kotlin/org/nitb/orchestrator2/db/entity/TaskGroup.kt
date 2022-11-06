package org.nitb.orchestrator2.db.entity

import io.micronaut.core.annotation.Introspected
import org.hibernate.Hibernate
import java.time.ZonedDateTime
import javax.persistence.*

@Introspected
@Entity
@Table(name = "task_groups")
@IdClass(TaskGroupPk::class)
data class TaskGroup (
    @Id
    val name: String,
    @Id
    val userGroup: String,
    var active: Boolean = true,
    val creationDate: ZonedDateTime = ZonedDateTime.now(),
    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(name = "task_like", joinColumns = [JoinColumn(name = "group_name"), JoinColumn(name = "group_user")],
        inverseJoinColumns = [JoinColumn(name = "task_name"), JoinColumn(name = "task_user")])
    val tasks: MutableSet<Task> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TaskGroup

        return name == other.name
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(name = $name , creationDate = $creationDate , userGroup = $userGroup )"
    }
}