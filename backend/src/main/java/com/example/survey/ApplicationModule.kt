package com.example.survey

import com.example.survey.api.apiRoutes
import com.example.survey.model.Response
import com.example.survey.model.UserPrincipal
import com.example.survey.www.protectedWwwRoutes
import com.example.survey.www.wwwRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.session
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.velocity.Velocity
import io.ktor.server.velocity.VelocityContent
import org.slf4j.LoggerFactory
import java.io.File

private val _LOG = LoggerFactory.getLogger(Application::class.java)
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        json(jsonInstance)
    }

    install(StatusPages) {
        status(HttpStatusCode.Forbidden) { call, _ ->
            call.respond(VelocityContent("error-403.html", mutableMapOf()))
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(VelocityContent("error-404.html", mutableMapOf()))
        }
        status(HttpStatusCode.InternalServerError) { call, _ ->
            call.respond(VelocityContent("error-500.html", mutableMapOf()))
        }
        exception<Throwable> { call, cause ->
            _LOG.error("webServerModule", cause)
            call.request.uri

            call.respond(
                HttpStatusCode.BadRequest,
                Response<Boolean>(null, cause.localizedMessage, false)
            )
        }
    }

    install(Velocity) {
        setProperty("resource.loader.file.path", "./templates")
    }

    install(Sessions) {
        cookie<UserPrincipal>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60
        }
    }

    install(Authentication) {
        session<UserPrincipal> {
            validate { session ->
                if(session.userName.startsWith("jet")) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }

    routing {
        apiRoutes()

        wwwRoutes()

        static()

        authenticate {
            protectedWwwRoutes()
        }
    }
}

fun Route.static() {
    staticFiles("/assets", File("./static"))
}
