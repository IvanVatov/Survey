package com.example.model

import io.ktor.server.auth.Principal
import kotlinx.serialization.Serializable

@Serializable
data class User(val userName: String) : Principal