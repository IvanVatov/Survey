package com.example.survey.www

import com.example.survey.www.route.dashboard
import com.example.survey.www.route.logout
import com.example.survey.www.route.myAccount
import com.example.survey.www.route.surveyDelete
import com.example.survey.www.route.surveyList
import com.example.survey.www.route.surveyResults
import com.example.survey.www.route.userDelete
import com.example.survey.www.route.userDetails
import com.example.survey.www.route.userList
import io.ktor.server.routing.Route

fun Route.protectedWwwRoutes() {
    logout()
    dashboard()
    surveyList()
    surveyDelete()
    surveyResults()
    myAccount()
    userList()
    userDetails()
    userDelete()
}