package com.example.survey.www.route

import com.example.survey.database.table.AnswerTable
import com.example.survey.database.table.QuestionTable
import com.example.survey.database.table.SurveyTable
import com.example.survey.jsonInstance
import com.example.survey.model.Answer
import com.example.survey.model.Question
import com.example.survey.model.Survey
import com.example.survey.model.UserPrincipal
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.velocity.VelocityContent
import java.nio.charset.StandardCharsets
import java.util.Base64


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

        val surveyEntries = if (principal.role == 1) {
            SurveyTable.getAll()
        } else {
            SurveyTable.getAllUser(principal.account)
        }

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
        val id = call.request.queryParameters["id"]?.toInt()
            ?: throw MissingRequestParameterException("id")
        val survey = SurveyTable.getById(id) ?: throw NotFoundException()
        call.respond(
            VelocityContent(
                "survey-answers.html",
                mutableMapOf("user" to principal, "survey" to survey)
            )
        )
    }
}

fun Route.surveyCreate() {
    get("/survey/create") {
        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        call.respond(
            VelocityContent(
                "survey-create.html",
                mutableMapOf("user" to principal)
            )
        )
    }
    post("/survey/create") {
        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        val params = call.receiveParameters()

        val finish = params["finish"]

        if (finish == "1") {
            val store = params["store"]

            var survey = jsonInstance.decodeFromString(
                Survey.serializer(),
                Base64.getDecoder().decode(store).toString(StandardCharsets.UTF_8)
            )

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
            call.respondRedirect("/survey/list")
            return@post
        }

        val temp = params["temp"]

        val name = params["name"]

        val question = params["question"]

        val isSingle = params["single"]

        val answer1 = params["answer1"]

        if (!temp.isNullOrBlank() && !question.isNullOrBlank() && !answer1.isNullOrBlank()) {

            var survey = jsonInstance.decodeFromString(
                Survey.serializer(),
                Base64.getDecoder().decode(temp).toString(StandardCharsets.UTF_8)
            )

            val mutable = survey.questions.toMutableList()

            val answers = mutableListOf(Answer(0, answer1))

            val answer2 = params["answer2"]
            if (!answer2.isNullOrBlank()) {
                answers.add(Answer(0, answer2))
            }

            val answer3 = params["answer3"]
            if (!answer3.isNullOrBlank()) {
                answers.add(Answer(0, answer3))
            }

            val answer4 = params["answer4"]
            if (!answer4.isNullOrBlank()) {
                answers.add(Answer(0, answer4))
            }

            val answer5 = params["answer5"]
            if (!answer5.isNullOrBlank()) {
                answers.add(Answer(0, answer5))
            }

            mutable.add(Question(0, question, isSingle == "on", answers))

            survey = survey.copy(questions = mutable)


            val json = jsonInstance.encodeToString(Survey.serializer(), survey)

            call.respond(
                VelocityContent(
                    "survey-create.html",
                    mutableMapOf(
                        "user" to principal,
                        "survey" to survey,
                        "json" to Base64.getEncoder()
                            .encodeToString(json.toByteArray(StandardCharsets.UTF_8))
                    )
                )
            )

            return@post
        }

        if (!name.isNullOrBlank()) {
            val survey = Survey(0, name, principal.account, emptyList())
            val json = jsonInstance.encodeToString(Survey.serializer(), survey)
            call.respond(
                VelocityContent(
                    "survey-create.html",
                    mutableMapOf(
                        "user" to principal,
                        "survey" to survey,
                        "json" to Base64.getEncoder()
                            .encodeToString(json.toByteArray(StandardCharsets.UTF_8))
                    )
                )
            )
        } else {
            call.respond(
                VelocityContent(
                    "survey-create.html",
                    mutableMapOf("user" to principal)
                )
            )
        }
    }
}