package com.example.survey.model

import com.example.survey.model.Answer
import kotlinx.serialization.Serializable

@Serializable
class Question(
    val id: Int,
    val question: String,
    val isSingle: Boolean,
    val answers: List<Answer>
)