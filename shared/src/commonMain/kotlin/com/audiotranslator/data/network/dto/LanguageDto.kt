package com.audiotranslator.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LanguageDto(
    @SerialName("code") val code: String,
    @SerialName("name") val name: String
)

@Serializable
data class LanguagesResponseDto(
    @SerialName("languages") val languages: List<LanguageDto>
)
