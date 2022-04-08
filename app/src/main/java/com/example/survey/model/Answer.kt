package com.example.survey.model

import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    val id: Int,
    val answer: String,
    var count: Int? = null
)
