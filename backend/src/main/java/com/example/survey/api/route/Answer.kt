package com.example.survey.api.route

import com.example.survey.database.table.UserAnswerTable
import com.example.survey.database.table.UserSurveyTable
import com.example.survey.model.Response
import com.example.survey.model.UserSurvey
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.apiSendAnswer() {
    post("/api/answer/send") {

        val userSurvey = call.receive<UserSurvey>()

        UserSurveyTable.insert(userSurvey)?.let { userSurveyId ->

            userSurvey.userAnswers.forEach { userAnswer ->
                UserAnswerTable.insert(userAnswer, userSurveyId)
            }
        }
        call.respond(Response(true, null, true))

        println("New Survey result was sent \n$userSurvey")
    }
}