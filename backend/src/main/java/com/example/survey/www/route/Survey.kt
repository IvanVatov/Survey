package com.example.survey.www.route

import com.example.survey.database.table.AnswerTable
import com.example.survey.database.table.QuestionTable
import com.example.survey.database.table.SurveyTable
import com.example.survey.model.Response
import com.example.survey.model.Survey
import com.example.survey.model.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.velocity.VelocityContent


fun Route.dashboard() {
    get("/") {
        val dashboardInfo = SurveyTable.getDashboardInfo()

        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        call.respond(
            VelocityContent(
                "index.html",
                mutableMapOf("user" to principal, "dashboard" to dashboardInfo)
            )
        )
    }
}

fun Route.surveyList() {
    get("/survey/list") {

        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        val surveyEntries = SurveyTable.getAll()

        call.respond(
            VelocityContent(
                "survey-list.html",
                mutableMapOf("user" to principal, "entries" to surveyEntries)
            )
        )
    }
}

fun Route.surveyDelete() {
    get("/survey/delete") {

        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        val deleteId = call.request.queryParameters["id"]?.toInt() ?: throw Exception()

        if (principal.role == 1) {
            SurveyTable.deleteAdmin(deleteId)
        } else if (principal.role == 0) {
            SurveyTable.deleteUser(deleteId, principal.account)
        }

        call.respondRedirect("/survey/list")
    }
}


fun Route.surveyResults() {
    get("/survey/result") {
        val principal = call.principal<UserPrincipal>() ?: throw Exception()
        val id = call.request.queryParameters["id"]?.toInt() ?: throw MissingRequestParameterException("id")
        val survey = SurveyTable.getById(id) ?: throw NotFoundException()
        call.respond(
            VelocityContent(
                "survey-answers.html",
                mutableMapOf("user" to principal, "survey" to survey)
            )
        )
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

        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        val survey = call.receive<Survey>()

        val surveyId = SurveyTable.insert(survey, principal.account)

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