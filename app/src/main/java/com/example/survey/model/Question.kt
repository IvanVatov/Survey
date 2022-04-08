package com.example.survey.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Int,
    val question: String,
    val isSingle: Boolean,
    val answers: List<Answer>
)