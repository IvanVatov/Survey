package com.example.model

import kotlinx.serialization.Serializable

@Serializable
class Answer(
    val answerId: Int,
    val answer: String
)
