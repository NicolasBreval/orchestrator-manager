package org.nitb.orchestrator2.grpc.client

import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.nitb.orchestrator2.service.TaskServiceGrpc
import org.nitb.orchestrator2.service.TaskServiceGrpc.TaskServiceBlockingStub

@Singleton
class TaskServiceClientGenerator(
    @Value("\${grpc.client.plaintext}") private val plainText: Boolean,
    @Value("\${grpc.client.max-retry-attempts}") private val maxRetryAttempts: Int?
): GrpcServiceClientGenerator<AbstractBlockingStub<TaskServiceBlockingStub>>(plainText, maxRetryAttempts) {

    override fun newSyncClient(server: String, port: Int): TaskServiceBlockingStub {
        return TaskServiceGrpc.newBlockingStub(getChannel(server, port))
    }

}