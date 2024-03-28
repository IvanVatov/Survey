package com.example.survey.www

import com.example.survey.www.route.getAllSurvey
import io.ktor.server.routing.Route

fun Route.protectedWwwRoutes() {
    getAllSurvey()
}