package com.audiotranslator.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("gender") val gender: String? = null
)

@Serializable
data class VoicesResponseDto(
    @SerialName("voices") val voices: List<VoiceDto>
)
