package com.example.survey.api

import com.example.survey.api.route.answer
import com.example.survey.api.route.survey
import com.example.survey.api.route.token
import com.example.survey.api.route.user
import com.example.survey.database.table.UserTable
import com.example.survey.jsonInstance
import freemarker.cache.ClassTemplateLoader
import freemarker.core.HTMLOutputFormat
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*


fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        json(jsonInstance)
    }

    install(StatusPages) {
        exception<Throwable> { e ->
            call.respond(
                HttpStatusCode.BadRequest,
                Response<Boolean>(null, e.localizedMessage, false)
            )
        }
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        templateUpdateDelayMilliseconds = 0L
        outputFormat = HTMLOutputFormat.INSTANCE
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
    static("/static") {
        resources("files")
    }
}
