package com.example.survey

import com.example.survey.database.Database
import io.ktor.server.application.Application
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import kotlinx.serialization.json.Json
import java.io.File
import java.security.KeyStore


val jsonInstance = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
    encodeDefaults = true
}

fun main(args: Array<String>) {

    Database

    val environment = applicationEngineEnvironment {

        connector {
            port = 80
        }
        val keyStoreFile = File("./survey.jks")

        sslConnector(
            keyStore = KeyStore.getInstance(keyStoreFile, "survey".toCharArray()),
            keyAlias = "survey",
            keyStorePassword = { "survey".toCharArray() },
            privateKeyPassword = { "survey".toCharArray() }) {
            port = 443
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }

    embeddedServer(Netty, environment).start(wait = true)
}