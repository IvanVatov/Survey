package com.example.survey

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.survey.ui.HomeScreen
import com.example.survey.ui.SurveyScreen

@Composable
fun SurveyApp() {

    var screenState by remember {
        ApplicationState.screenState
    }

    when(screenState) {
        ApplicationState.Screen.HOME -> HomeScreen()
        else -> SurveyScreen()
    }
}