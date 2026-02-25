package com.audiotranslator.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranscriptionResponseDto(
    @SerialName("original_text") val transcription: String,
    @SerialName("detected_language") val detectedLanguage: String? = null
)
