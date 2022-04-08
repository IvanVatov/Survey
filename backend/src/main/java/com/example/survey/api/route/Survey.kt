package com.example.survey.api.route

import com.example.model.Survey
import com.example.survey.api.Response
import com.example.survey.database.table.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.survey() {
    routing {
        getSurveyId()
        createSurvey()
        getAllSurvey()
        getResults()

    }
}

fun Route.getAllSurvey() {
    get("/") {
        val surveyEntries = SurveyTable.getAll()
        call.respond(FreeMarkerContent("index.ftl", mapOf("entries" to surveyEntries), ""))
    }
}

fun Route.getResults() {
    get("/results") {
        val id = call.request.queryParameters["id"]?.toInt() ?: 1
        val survey = SurveyTable.getById(id)
        call.respond(FreeMarkerContent("results.ftl", mapOf("survey" to survey), ""))
    }
}

fun Route.getSurveyId() {
    get("/survey") {
        call.request.queryParameters["id"]?.let {
            call.respond(Response(SurveyTable.getById(it.toInt()), null, true))

            println("Client request getSurveyId $it")
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

        println("New Survey created \n$survey")

        call.respond(Response(surveyId, null, true))
    }
}