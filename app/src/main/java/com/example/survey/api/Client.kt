package com.example.survey.api

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val jsonInstance = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

val httpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(jsonInstance)
    }

    engine {
        preconfigured = getUnsafeOkHttpClient()
    }
}

fun getUnsafeOkHttpClient(): OkHttpClient {

    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
    })


    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, java.security.SecureRandom())

    val sslSocketFactory = sslContext.socketFactory

    return OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }.build()
}