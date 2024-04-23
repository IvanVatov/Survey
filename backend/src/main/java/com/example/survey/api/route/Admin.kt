package com.example.survey.api.route

import com.example.survey.database.table.UserTable
import com.example.survey.model.Admin
import com.example.survey.model.Response
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.apiSetAdmin() {
    post("/api/admin/set") {

        val admin = call.receive<Admin>()

        val result = UserTable.setRole(1, admin.account)

        call.respond(Response(result, null, true))
    }
}