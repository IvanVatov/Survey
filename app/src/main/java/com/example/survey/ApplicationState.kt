package com.example.survey

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.survey.api.Response
import com.example.survey.api.httpClient
import com.example.survey.model.Question
import com.example.survey.model.Survey
import com.example.survey.model.UserAnswer
import com.example.survey.model.UserSurvey
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch

object ApplicationState : ViewModel() {

    enum class Screen {
        HOME, START, SURVEY, FINISH
    }

    val screenState = mutableStateOf(Screen.HOME)

    val currentQuestion = mutableStateOf<Question?>(null)

    val questionAnswers = mutableStateOf<List<Int>>(emptyList())

    var currentQuestionIndex = mutableStateOf(0)

    val survey = mutableStateOf<Survey?>(null)

    var userName = ""

    fun loadSurvey(id: Int) {

        viewModelScope.launch {
            val response: Response<Survey>? =
                try {
                    httpClient.get(Configuration.SERVER_API_GET_SURVEY) {
                        parameter("id", id)
                    }
                } catch (e: Throwable) {
                    null
                }
            survey.value = response?.result
            screenState.value = Screen.START
        }
    }

    fun startSurvey(userName: String) {
        this.userName = userName
        currentQuestion.value = survey.value?.questions?.first()

        screenState.value = Screen.SURVEY
    }

    fun nextQuestion() {
        currentQuestionIndex.value = currentQuestionIndex.value + 1
        currentQuestion.value =
            survey.value?.questions?.getOrNull(currentQuestionIndex.value) ?: let {
                screenState.value = Screen.FINISH
                null
            }
    }

    fun previewsQuestion() {
        currentQuestionIndex.value = currentQuestionIndex.value - 1
        currentQuestion.value =
            survey.value?.questions?.getOrNull(currentQuestionIndex.value) ?: let {
                null
            }
    }

    fun sendResult() {
        val mSurvey = survey.value ?: return

        val mAnswers = questionAnswers.value.map { UserAnswer(it) }

        val userSurvey = UserSurvey(0, mSurvey.id, userName, mAnswers)

        viewModelScope.launch {
            val response: Response<Boolean>? =
                try {
                    httpClient.post(Configuration.SERVER_API_POST_RESULT) {
                        contentType(ContentType.Application.Json)
                        body = userSurvey
                    }
                } catch (e: Throwable) {
                    null
                }

            screenState.value = Screen.HOME
        }

        survey.value = null
        currentQuestion.value = null
        questionAnswers.value = emptyList()
        currentQuestionIndex.value = 0

        screenState.value = Screen.HOME
    }

    fun nextEnabled(): Boolean {
        var result = false
        currentQuestion.value?.answers?.forEach { answer ->
            if (questionAnswers.value.contains(answer.id))
                result = true
        }
        return result
    }

}