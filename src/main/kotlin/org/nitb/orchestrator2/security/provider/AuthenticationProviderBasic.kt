package org.nitb.orchestrator2.security.provider

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import jakarta.annotation.PostConstruct
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.nitb.orchestrator2.security.model.User
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.io.File

@Singleton
@Requires(property = "micronaut.security.authentication", value = "cookie")
@Suppress("UNUSED")
class AuthenticationProviderBasic: AuthenticationProvider {
    override fun authenticate(
        httpRequest: HttpRequest<*>?,
        authenticationRequest: AuthenticationRequest<*, *>?
    ): Publisher<AuthenticationResponse> {
        return Flux.create({ emitter: FluxSink<AuthenticationResponse> ->
            val user = users.firstOrNull { it.name == authenticationRequest?.identity }
            if (authenticationRequest?.secret == user?.password) {
                emitter.next(AuthenticationResponse.success(authenticationRequest?.identity as String, user?.roles?.map { it.name } ?: listOf(), mapOf("group" to user?.group)))
                emitter.complete()
            } else {
                emitter.error(AuthenticationResponse.exception())
            }
            emitter.next(AuthenticationResponse.success(authenticationRequest?.identity as String))
            emitter.complete()
        }, FluxSink.OverflowStrategy.ERROR)
    }

    @PostConstruct
    fun postConstruct() {
        File(usersFile).let { file ->
            users = if (file.exists()) {
                jsonMapper.readValue(file, object: TypeReference<List<User>>() {})
            } else {
                listOf()
            }
        }
    }

    @Value("\${orchestrator.security.basic-users-file}")
    private lateinit var usersFile: String

    @Inject
    private lateinit var jsonMapper: ObjectMapper

    @Inject
    private lateinit var users: List<User>
}