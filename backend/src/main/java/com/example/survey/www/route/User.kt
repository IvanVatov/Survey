package com.example.survey.www.route

import com.example.survey.database.table.UserTable
import com.example.survey.model.UserPrincipal
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.velocity.VelocityContent
import kotlin.random.Random

fun Route.login() {
    get("/login") {
        call.respond(VelocityContent("auth-login.html", mutableMapOf()))
    }
    post("/login") {
        val params = call.receiveParameters()

        val userName = params["account"]

        val password = params["password"]

        if (userName != null && password != null) {
            val user = UserTable.getByCredential(userName, password)

            if (user != null) {
                call.sessions.set(user)
                call.respondRedirect("/")
                return@post
            }

        }

        call.respond(VelocityContent("auth-login.html", mutableMapOf()))
    }
}

fun Route.logout() {
    get("/logout") {
        call.sessions.clear<UserPrincipal>()
        call.respondRedirect("/login")
    }

}

fun Route.register() {
    get("/register") {
        call.respond(VelocityContent("auth-register.html", mutableMapOf()))
    }
    post("/register") {
        val params = call.receiveParameters()

        val name = params["name"] ?: throw MissingRequestParameterException("name")

        val account = params["account"] ?: throw MissingRequestParameterException("account")

        val password = params["password"] ?: throw MissingRequestParameterException("password")

        val password2 = params["password2"] ?: throw MissingRequestParameterException("password2")

        if (password == password2) {

            val avatar = "/assets/compiled/jpg/${Random.nextInt(8) + 1}.jpg"
            val user = UserTable.insert(name, account, password, avatar)

            if (user != null) {
                call.sessions.set(UserPrincipal(account, name, avatar, 0))
                call.respondRedirect("/")
                return@post
            }

        }

        call.respond(VelocityContent("auth-register.html", mutableMapOf()))
    }
}

fun Route.myAccount() {
    get("/account") {
        val principal = call.principal<UserPrincipal>() ?: throw Exception()
        call.respond(
            VelocityContent(
                "my-account.html",
                mutableMapOf("user" to principal)
            )
        )
    }
    post("/account") {

        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        val params = call.receiveParameters()

        val name = params["name"] ?: throw MissingRequestParameterException("name")

        val avatar = params["avatar"] ?: throw MissingRequestParameterException("avatar")

        val password = params["password"] ?: throw MissingRequestParameterException("password")

        val password2 = params["password2"] ?: throw MissingRequestParameterException("password2")

        var error: String? = null

        if (password.isNotBlank() && password != password2) {
            error = "Passwords doesn't match."
        } else if (name.isBlank()) {
            error = "Name is required field."
        } else if (avatar.isBlank()) {
            error = "Avatar is required field"
        }

        if (error != null) {
            call.respond(
                VelocityContent(
                    "my-account.html",
                    mutableMapOf("user" to principal, "error" to error)
                )
            )
            return@post
        }

        if (password.isNotBlank()) {
            UserTable.updateUserPassword(password, principal.account)
        }

        UserTable.updateUser(name, avatar, principal.account)

        call.sessions.set(principal.copy(name = name, avatar = avatar))
        call.respondRedirect("/account")
    }
}

fun Route.userList() {
    get("/user/list") {

        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        if (principal.role < 1) {
            throw Exception()
        }

        val users = UserTable.getAllUsers()

        call.respond(
            VelocityContent(
                "user-list.html",
                mutableMapOf("user" to principal, "entries" to users)
            )
        )
    }
}

fun Route.userDetails() {
    get("/user/details") {

        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        if (principal.role < 1) {
            throw Exception()
        }

        val account =
            call.request.queryParameters["account"]
                ?: throw MissingRequestParameterException("account")

        val user = UserTable.getByAccount(account) ?: throw NotFoundException()

        call.respond(
            VelocityContent(
                "user-details.html",
                mutableMapOf("user" to principal, "item" to user)
            )
        )
    }
    post("/user/details") {
        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        if (principal.role < 1) {
            throw Exception()
        }

        val account =
            call.request.queryParameters["account"]
                ?: throw MissingRequestParameterException("account")

        val role = call.receiveParameters()["role"]?.toInt()
            ?: throw MissingRequestParameterException("role")

        UserTable.setRole(role, account)

        call.respondRedirect("/user/details?account=$account")
    }
}

fun Route.userDelete() {
    get("/user/delete") {

        val principal = call.principal<UserPrincipal>() ?: throw Exception()

        if (principal.role < 1) {
            throw Exception()
        }
        val account =
            call.request.queryParameters["account"]
                ?: throw MissingRequestParameterException("account")

        UserTable.delete(account)

        call.respondRedirect("/user/list")
    }
}
