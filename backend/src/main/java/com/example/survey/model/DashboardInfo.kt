package com.example.survey.model

data class DashboardInfo(
    val users: Int,
    val surveys: Int,
    val questions: Int,
    val answers: Int,
    val answered: Int
)