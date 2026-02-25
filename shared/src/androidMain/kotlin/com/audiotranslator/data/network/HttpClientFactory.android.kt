package com.audiotranslator.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(block)
