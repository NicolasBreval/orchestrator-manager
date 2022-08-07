package org.nitb.orchestrator2.db.entity

import org.hibernate.Hibernate
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "task_historicals")
@Table(name = "task_historicals")
data class TaskHistorical(
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: BigDecimal? = null,

    @Column(nullable = false)
    var date: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false, length = 100)
    var username: String = "",

    @Lob
    @Column(nullable = false)
    var parameters: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var operation: TaskOperation? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "task_id", insertable = true, updatable = false, nullable = false)
    var task: Task? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TaskHistorical

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , date = $date , username = $username , parameters = $parameters , operation = $operation )"
    }
}