package com.example.survey.model

import io.ktor.server.auth.Principal
import kotlinx.serialization.Serializable

@Serializable
data class UserPrincipal(val userName: String) : Principal