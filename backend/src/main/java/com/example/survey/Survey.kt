package com.example.survey

import com.example.survey.database.Database
import kotlinx.serialization.json.Json


val jsonInstance = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
}

fun main(args: Array<String>) {
    Database
    io.ktor.server.netty.EngineMain.main(args)
}