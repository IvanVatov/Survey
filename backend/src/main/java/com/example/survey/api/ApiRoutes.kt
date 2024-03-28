package com.example.survey.api


import com.example.survey.api.route.apiCreateSurvey
import com.example.survey.api.route.apiGetSurveyId
import com.example.survey.api.route.apiSendAnswer
import io.ktor.server.routing.Route

fun Route.apiRoutes() {
    apiSendAnswer()
    apiGetSurveyId()
    apiCreateSurvey()
}
