package com.example.survey.model

import com.example.survey.model.Question
import kotlinx.serialization.Serializable

@Serializable
class Survey(
    val id: Int,
    val name: String,
    val questions: List<Question>
)