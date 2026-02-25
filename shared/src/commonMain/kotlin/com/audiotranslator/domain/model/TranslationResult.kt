package com.audiotranslator.domain.model

data class TranslationResult(
    val transcription: String,
    val translation: String? = null,
    val detectedLanguage: String? = null,
    val targetLanguage: String? = null
)
