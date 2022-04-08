package com.example.survey.model

import kotlinx.serialization.Serializable

@Serializable
data class Survey(
    val id: Int,
    val name: String,
    val questions: List<Question>
)