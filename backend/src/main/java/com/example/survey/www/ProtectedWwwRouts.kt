package com.example.survey.www

import com.example.survey.www.route.dashboard
import com.example.survey.www.route.surveyDelete
import com.example.survey.www.route.surveyList
import com.example.survey.www.route.surveyResults
import io.ktor.server.routing.Route

fun Route.protectedWwwRoutes() {
    dashboard()
    surveyList()
    surveyDelete()
    surveyResults()
}