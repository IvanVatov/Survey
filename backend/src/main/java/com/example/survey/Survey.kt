package com.example.survey

import kotlinx.serialization.json.Json


val jsonInstance = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
}

fun main(args: Array<String>) {

    io.ktor.server.netty.EngineMain.main(args)
}