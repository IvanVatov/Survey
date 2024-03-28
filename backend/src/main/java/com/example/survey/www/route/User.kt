package com.example.survey.www.route

import com.example.survey.model.Response
import com.example.survey.database.table.UserTable
import com.example.survey.model.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.velocity.VelocityContent

fun Route.login() {
    get("/login") {
        call.respond(VelocityContent("auth-login.html", mutableMapOf()))
    }
    post("/login") {
        val params = call.receiveParameters()

        val userName = params["account"]

        val password = params["password"]

        if (userName != null && password != null) {
           val user = UserTable.getByCredential(userName, password)

            call.sessions.set(user)
            call.respondRedirect("/")
        }

        call.respond(VelocityContent("auth-login.html", mutableMapOf()))
    }
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