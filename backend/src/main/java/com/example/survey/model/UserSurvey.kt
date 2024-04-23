package com.example.survey.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSurvey(val id: Int, val surveyId: Int, val userName: String, val userAnswers: List<UserAnswer>)