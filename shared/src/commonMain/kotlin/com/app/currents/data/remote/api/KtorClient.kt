package com.app.currents.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(): HttpClient = HttpClient {

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true   // NewsData.io adds new fields sometimes — don't crash
            isLenient = true           // handles slightly malformed JSON gracefully
            explicitNulls = false      // missing fields treated as null, not errors
        })
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.HEADERS       // shows request/response headers in Logcat, not full body
    }
}