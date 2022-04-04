package com.example.model

import kotlinx.serialization.Serializable

@Serializable
class Question(
    val id: Int,
    val question: String,
    val isSingle: Boolean,
    val answers: List<Answer>
)