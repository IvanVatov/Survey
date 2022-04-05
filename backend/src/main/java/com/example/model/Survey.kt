package com.example.model

import kotlinx.serialization.Serializable

@Serializable
class Survey(
    val id: Int,
    val name: String,
    val questions: List<Question>
)