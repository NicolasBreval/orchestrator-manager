package org.nitb.orchestrator2.db.entity

import org.hibernate.Hibernate
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "tasks")
@Table(name = "tasks")
data class Task(
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: BigDecimal? = null,

    @Column(nullable = false, unique = true, length = 500)
    var name: String = "",

    @Column(name = "creation_date", nullable = false)
    var creationDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "last_modification", nullable = false)
    var lastModification: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(nullable = false)
    var stopped: Boolean = false,

    @Column(nullable = false, length = 500)
    var type: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Task

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(name = $name , creationDate = $creationDate , lastModification = $lastModification , active = $active , stopped = $stopped , type = $type )"
    }
}