package org.nitb.orchestrator2.mq

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.nitb.orchestrator2.model.WorkerInfo
import org.nitb.orchestrator2.service.WorkerService
import org.nitb.orchestrator2.task.mq.model.MQMessage
import org.slf4j.LoggerFactory

@Singleton
open class BaseListener(
    private val jsonMapper: ObjectMapper
) {

    fun processData(data: ByteArray) {
        logger.debug("New worker info received!!!")
        try {
            val message = jsonMapper.readValue(data, object : TypeReference<MQMessage<WorkerInfo>>() {})
            logger.debug("Registering worker information...")
            if (message.message != null)
                workerService.registerWorker(message.message!!)
            else
                logger.warn("Received null message, it won't be processed")
        } catch (e: Exception) {
            logger.error("Fatal exception reading worker info message: ${e.message}", e)
        }
    }

    @Inject
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Inject
    private lateinit var workerService: WorkerService
}