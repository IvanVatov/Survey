package com.example.survey.api.route

import com.example.model.Survey
import com.example.survey.api.Response
import com.example.survey.database.table.AnswerTable
import com.example.survey.database.table.QuestionTable
import com.example.survey.database.table.SurveyTable
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.survey() {
    routing {
        getSurveyId()
        createSurvey()
    }
}

fun Route.getSurveyId() {
    get("/survey") {
        call.request.queryParameters["id"]?.let {
            call.respond(Response(SurveyTable.getById(it.toInt()), null, true))
        } ?: call.respond(
            HttpStatusCode.BadRequest,
            Response<Boolean>(null, "Parameter id is required", false)
        )
    }
}

fun Route.createSurvey() {
    post("/survey/create") {

        val survey = call.receive<Survey>()

        val surveyId = SurveyTable.insert(survey)

        surveyId?.let { sId ->
            survey.questions.forEach { question ->
                QuestionTable.insert(question, sId)?.let { questionId ->
                    question.answers.forEach { answer ->
                        AnswerTable.insert(answer, questionId)
                    }
                }
            }
        }

        call.respond(Response(surveyId, null, true))
    }
}