package com.example.survey.api.route

import com.example.survey.api.AuthCredential
import com.example.survey.api.JwtConfig
import com.example.survey.api.TokenResponse
import com.example.survey.api.validate
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.token() {

    route("/token") {
        post {

            val credentials = call.receive<AuthCredential>()

            val token = JwtConfig.makeToken(credentials.validate())

            call.respond(TokenResponse(token))
        }
    }
}