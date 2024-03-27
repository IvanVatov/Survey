package com.example.survey.api.route

import com.example.survey.api.AuthCredential
import com.example.survey.api.JwtConfig
import com.example.survey.api.TokenResponse
import com.example.survey.api.validate
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.token() {

    route("/token") {
        post {

            val credentials = call.receive<AuthCredential>()

            val token = JwtConfig.makeToken(credentials.validate())

            call.respond(TokenResponse(token))
        }
    }
}