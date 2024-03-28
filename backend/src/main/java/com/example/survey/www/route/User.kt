package com.example.survey.www.route

import com.example.survey.model.Response
import com.example.survey.database.table.UserTable
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.user() {
    getId()
    getAll()
}

fun Route.getId() {
    get("/user") {
        call.request.queryParameters["id"]?.let {
            call.respond(Response("asd", null, true))
        } ?: call.respond(
            HttpStatusCode.BadRequest,
            Response<Boolean>(null, "Parameter id is required", false)
        )
    }
}

fun Route.getAll() {
    get("/user/all") {
        call.respond(Response(UserTable.getAllUsers(), null, true))
    }
}