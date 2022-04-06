package com.example.survey.api.route

import com.example.model.UserAnswer
import com.example.model.UserSurvey
import com.example.survey.api.Response
import com.example.survey.database.table.UserAnswerTable
import com.example.survey.database.table.UserSurveyTable
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.answer() {
    routing {
        sendAnswer()
        getAnswerId()
    }
}

fun Route.getAnswerId() {
    get("/answer") {
        // TODO
    }
}

fun Route.sendAnswer() {
    post("/answer/send") {

        val userSurvey = call.receive<UserSurvey>()

        UserSurveyTable.insert(userSurvey)?.let { userSurveyId ->

            userSurvey.userAnswers.forEach { userAnswer ->
                UserAnswerTable.insert(userAnswer, userSurveyId)
            }
        }
        call.respond(Response(true, null, true))
    }
}