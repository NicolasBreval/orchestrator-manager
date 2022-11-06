package org.nitb.orchestrator2.mq.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Requires
import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener
import org.nitb.orchestrator2.mq.BaseListener

@Suppress("UNUSED")
@RabbitListener
@Requires(property = "rabbitmq.enabled", value = "true")
class ManagerListener(
    jsonMapper: ObjectMapper
): BaseListener(jsonMapper) {

    @Queue("\${orchestrator.mq.manager.queue}")
    fun receive(data: ByteArray) {
        processData(data)
    }
}