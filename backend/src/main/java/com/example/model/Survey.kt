package com.example.model

import kotlinx.serialization.Serializable

@Serializable
class Survey(
    val surveyId: Int,
    val questions: List<Question>
)