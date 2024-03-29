package com.example.survey.www

import com.example.survey.www.route.login
import com.example.survey.www.route.logout
import com.example.survey.www.route.register
import io.ktor.server.routing.Route

fun Route.wwwRoutes() {
    login()
    logout()
    register()
}