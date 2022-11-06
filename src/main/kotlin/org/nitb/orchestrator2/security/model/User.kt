package org.nitb.orchestrator2.security.model

data class User (
    val name: String,
    val password: String,
    val roles: List<Role>,
    val group: String
)