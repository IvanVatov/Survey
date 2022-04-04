package com.example.survey.api.route

import com.example.survey.api.Response
import com.example.survey.database.table.UserTable
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.survey() {
    routing {
        getId()
        getAll()
    }
}

fun Route.getSurveyId() {
    get("/user") {
        call.request.queryParameters["id"]?.let {
            call.respond(Response("asd", null, true))
        } ?: call.respond(HttpStatusCode.BadRequest, Response<Boolean>(null, "Parameter id is required", false))
    }
}