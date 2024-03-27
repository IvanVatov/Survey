package com.example.survey.api.route

import com.example.model.Survey
import com.example.survey.api.Response
import com.example.survey.database.table.AnswerTable
import com.example.survey.database.table.QuestionTable
import com.example.survey.database.table.SurveyTable
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.velocity.VelocityContent

fun Route.survey() {
    getSurveyId()
    createSurvey()
    getAllSurvey()
    getResults()
}

fun Route.getAllSurvey() {
    get("/") {
        val surveyEntries = SurveyTable.getAll()
        call.respond(VelocityContent("index.html", mutableMapOf("entries" to surveyEntries)))
    }
}

fun Route.getResults() {
    get("/results") {
        val id = call.request.queryParameters["id"]?.toInt() ?: 1
        val survey =
            SurveyTable.getById(id) ?: throw Throwable("Implement null")//  TODO IMPLEMENT NULL
        call.respond(VelocityContent("results.html", mutableMapOf("survey" to survey)))
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