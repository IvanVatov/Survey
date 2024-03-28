package com.example.survey.model

import io.ktor.server.auth.Principal
import kotlinx.serialization.Serializable

@Serializable
data class User(val userName: String) : Principal