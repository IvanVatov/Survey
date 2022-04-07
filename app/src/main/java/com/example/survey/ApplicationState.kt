package com.example.survey

import androidx.compose.runtime.mutableStateOf

object ApplicationState {

    enum class Screen{
        HOME, START, SURVEY, FINISH
    }

    val screenState = mutableStateOf(Screen.HOME)
}