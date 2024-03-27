package com.example.survey.api

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.User
import com.example.survey.database.table.UserTable
import io.ktor.server.auth.Credential
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class TokenResponse(val token: String)

@Serializable
data class AuthCredential(val name: String, val password: String) : Credential

suspend fun AuthCredential.validate(): User {

    UserTable.getByCredential(this)?.let {
        return it
    }

    throw NullPointerException("Invalid credentials")
}


object JwtConfig {

    private const val secret = "secretString"
    private const val issuer = "surveyApi"
    private const val validityInMs = 36_000_00 * 24 * 365L * 10// 10 years

    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    /**
     * Produce a token
     */
    fun makeToken(user: User): String = JWT.create()
        .withSubject("Survey")
        .withIssuer(issuer)
        .withClaim("id", user.userName)
//        .withArrayClaim("countries", user.countries.toTypedArray())
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}