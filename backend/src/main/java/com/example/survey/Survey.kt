package com.example.survey

import com.example.survey.database.Database
import kotlinx.serialization.json.Json


val jsonInstance = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
    encodeDefaults = true
}

fun main(args: Array<String>) {
    println("Starting Backend application")
    Database
    io.ktor.server.netty.EngineMain.main(args)
}