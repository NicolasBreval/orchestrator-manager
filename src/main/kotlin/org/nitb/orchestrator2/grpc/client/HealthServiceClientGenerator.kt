package org.nitb.orchestrator2.grpc.client

import grpc.health.v1.HealthGrpc
import grpc.health.v1.HealthGrpc.HealthBlockingStub
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton

@Singleton
class HealthServiceClientGenerator(
    @Value("\${grpc.client.plaintext}") private val plainText: Boolean,
    @Value("\${grpc.client.max-retry-attempts}") private val maxRetryAttempts: Int?
): GrpcServiceClientGenerator<AbstractBlockingStub<HealthBlockingStub>>(plainText, maxRetryAttempts) {
    override fun newSyncClient(server: String, port: Int): HealthBlockingStub {
        return HealthGrpc.newBlockingStub(getChannel(server, port))
    }
}