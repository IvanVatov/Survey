package com.example.model

import io.ktor.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class User(val userName: String) : Principal