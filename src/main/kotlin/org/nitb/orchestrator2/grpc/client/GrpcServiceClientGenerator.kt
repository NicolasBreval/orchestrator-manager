package org.nitb.orchestrator2.grpc.client

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

abstract class GrpcServiceClientGenerator<T : io.grpc.stub.AbstractBlockingStub<*>>(
    private val plainText: Boolean,
    private val maxRetryAttempts: Int?
) {
    abstract fun newSyncClient(server: String, port: Int): T

    protected fun getChannel(server: String, port: Int): ManagedChannel {
        return ManagedChannelBuilder.forAddress(server, port).let {
            if (plainText)
                it.usePlaintext()
            else it
        }.let {
            if (maxRetryAttempts != null)
                it.maxRetryAttempts(maxRetryAttempts)
            else it
        }.build()
    }
}