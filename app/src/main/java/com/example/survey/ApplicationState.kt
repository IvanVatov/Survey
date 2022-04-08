package com.example.survey

import androidx.compose.runtime.mutableStateOf
import com.example.survey.model.Answer
import com.example.survey.model.Question
import com.example.survey.model.Survey

object ApplicationState {

    enum class Screen {
        HOME, START, SURVEY, FINISH
    }

    val screenState = mutableStateOf(Screen.HOME)

    val currentQuestion = mutableStateOf<Question?>(null)

    val currentQuestionAnswers = mutableStateOf<List<Int>>(emptyList())

    private val userAnswers = ArrayList<Int>()

    val survey = mutableStateOf<Survey?>(null)

    fun startSurvey() {

        survey.value =
            Survey(
                1,
                "Hello",
                listOf(Question(1, "Kvo staa", false, listOf(Answer(1, "Biva"), Answer(2, "Super"))))
            )

        currentQuestion.value = survey.value?.questions?.first()
        userAnswers.clear()

        screenState.value = Screen.SURVEY
    }

    fun nextQuestion() {
        currentQuestion.value = currentQuestion.value?.let {
            var current = survey.value?.questions?.lastIndexOf(it) ?: -1
            survey.value?.questions?.get(++current)
        }
    }

}