package com.audiotranslator.data.network

import com.audiotranslator.data.network.dto.LanguageDto
import com.audiotranslator.data.network.dto.LanguagesResponseDto
import com.audiotranslator.data.network.dto.TranscriptionResponseDto
import com.audiotranslator.data.network.dto.TranslationResponseDto
import com.audiotranslator.data.network.dto.VoiceDto
import com.audiotranslator.data.network.dto.VoicesResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class TranslatorApi {

    private val client = createHttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) = println("Ktor: $message")
            }
            level = LogLevel.HEADERS
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 120_000
        }

        defaultRequest {
            url(BASE_URL)
        }
    }

    suspend fun getHealth(): Boolean = runCatching {
        client.get("api/health").status.value in 200..299
    }.getOrDefault(false)

    suspend fun getLanguages(): List<LanguageDto> =
        client.get("api/languages").body<LanguagesResponseDto>().languages

    suspend fun getVoices(langCode: String): List<VoiceDto> =
        client.get("api/voices/$langCode").body<VoicesResponseDto>().voices

    suspend fun translateAudio(
        audioBytes: ByteArray,
        targetLanguage: String,
        voice: String?,
        fileName: String = "recording.m4a",
        mimeType: String = "audio/mp4"
    ): TranslationResponseDto {
        return client.submitFormWithBinaryData(
            url = "api/translate",
            formData = formData {
                append("file", audioBytes, Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        ) {
            parameter("target_language", targetLanguage)
            voice?.let { parameter("voice", it) }
        }.body()
    }

    suspend fun translateAudioRaw(
        audioBytes: ByteArray,
        targetLanguage: String,
        voice: String?,
        fileName: String = "recording.m4a",
        mimeType: String = "audio/mp4"
    ): ByteArray {
        return client.submitFormWithBinaryData(
            url = "api/translate/audio",
            formData = formData {
                append("file", audioBytes, Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        ) {
            parameter("target_language", targetLanguage)
            voice?.let { parameter("voice", it) }
        }.body()
    }

    suspend fun transcribeAudio(
        audioBytes: ByteArray,
        fileName: String = "recording.m4a",
        mimeType: String = "audio/mp4"
    ): TranscriptionResponseDto {
        return client.submitFormWithBinaryData(
            url = "api/transcribe",
            formData = formData {
                append("file", audioBytes, Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        ).body()
    }

    companion object {
        const val BASE_URL = "https://nav772-audio-language-translator.hf.space/"
    }
}
