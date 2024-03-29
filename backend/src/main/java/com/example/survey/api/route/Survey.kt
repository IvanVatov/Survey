package com.example.survey.api.route

import com.example.survey.database.table.AnswerTable
import com.example.survey.database.table.QuestionTable
import com.example.survey.database.table.SurveyTable
import com.example.survey.model.Response
import com.example.survey.model.Survey
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post


fun Route.apiGetSurveyId() {
    get("/api/survey") {
        call.request.queryParameters["id"]?.let {
            call.respond(Response(SurveyTable.getById(it.toInt()), null, true))

            println("Client request getSurveyId $it")
        } ?: call.respond(
            HttpStatusCode.BadRequest,
            Response<Boolean>(null, "Parameter id is required", false)
        )
    }
}

fun Route.apiCreateSurvey() {
    post("/api/survey/create") {

        val survey = call.receive<Survey>()

        val owner = call.request.queryParameters["owner"] ?: throw MissingRequestParameterException("owner")

        val surveyId = SurveyTable.insert(survey, owner)

        surveyId?.let { sId ->
            survey.questions.forEach { question ->
                QuestionTable.insert(question, sId)?.let { questionId ->
                    question.answers.forEach { answer ->
                        AnswerTable.insert(answer, questionId)
                    }
                }
            }
        }

        println("New Survey created \n$survey")

        call.respond(Response(surveyId, null, true))
    }
}