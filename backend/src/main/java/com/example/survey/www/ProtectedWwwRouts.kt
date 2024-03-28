package com.example.survey.www

import com.example.survey.www.route.dashboard
import com.example.survey.www.route.surveyList
import io.ktor.server.routing.Route

fun Route.protectedWwwRoutes() {
    dashboard()
    surveyList()
}