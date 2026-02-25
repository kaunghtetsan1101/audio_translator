package com.audiotranslator.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranslationResponseDto(
    @SerialName("original_text") val transcription: String,
    @SerialName("translated_text") val translation: String,
    @SerialName("detected_language") val detectedLanguage: String? = null,
    @SerialName("target_language") val targetLanguage: String? = null,
    @SerialName("voice") val voice: String? = null
)
