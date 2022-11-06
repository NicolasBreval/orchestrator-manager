package org.nitb.orchestrator2.mq.activemq

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Requires
import io.micronaut.jms.activemq.classic.configuration.ActiveMqClassicConfiguration.CONNECTION_FACTORY_BEAN_NAME
import io.micronaut.jms.annotations.JMSListener
import io.micronaut.jms.annotations.Queue
import io.micronaut.messaging.annotation.MessageBody
import org.nitb.orchestrator2.mq.BaseListener

@Suppress("UNUSED")
@JMSListener(CONNECTION_FACTORY_BEAN_NAME)
@Requires(property = "micronaut.jms.activemq.classic.enabled", value = "true")
class ManagerListener(
    jsonMapper: ObjectMapper
): BaseListener(jsonMapper) {

    @Queue("\${orchestrator.mq.manager.queue}", concurrency = "1-1", transacted = true)
    fun receive(@MessageBody body: String) {
        processData(body.toByteArray())
    }
}