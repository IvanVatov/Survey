package com.example.survey.api

import com.example.survey.api.route.answer
import com.example.survey.api.route.survey
import com.example.survey.api.route.token
import com.example.survey.api.route.user
import com.example.survey.database.table.UserTable
import com.example.survey.jsonInstance

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import io.ktor.server.velocity.Velocity
import org.slf4j.LoggerFactory
import java.io.File

private val _LOG = LoggerFactory.getLogger(Application::class.java)
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        json(jsonInstance)
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            _LOG.error("webServerModule", cause)
            call.respond(
                HttpStatusCode.BadRequest,
                Response<Boolean>(null, cause.localizedMessage, false)
            )
        }
    }

//    install(FreeMarker) {
//        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
//        templateUpdateDelayMilliseconds = 0L
//        outputFormat = HTMLOutputFormat.INSTANCE
//    }
    install(Velocity) {
        setProperty("resource.loader.file.path", "./templates")
    }

    install(Authentication) {
        jwt {
            // Configure jwt authentication
            verifier(JwtConfig.verifier)
            validate {
                it.payload.getClaim("id")?.let { claim ->
                    UserTable.getByUserName(claim.asString())
                }
            }
        }
    }

    routing {
        token()

        user()

        survey()

        answer()

        static()
//
//        authenticate {
//            user()
//        }
    }
}

fun Route.static() {
    staticFiles("/static", File("./static"))
}
