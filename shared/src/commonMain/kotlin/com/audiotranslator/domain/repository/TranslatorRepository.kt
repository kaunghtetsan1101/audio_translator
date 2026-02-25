package com.audiotranslator.domain.repository

import com.audiotranslator.domain.model.Language
import com.audiotranslator.domain.model.TranslationResult
import com.audiotranslator.domain.model.Voice

interface TranslatorRepository {
    suspend fun getLanguages(): Result<List<Language>>
    suspend fun getVoices(langCode: String): Result<List<Voice>>
    suspend fun translateAudio(
        audioBytes: ByteArray,
        targetLanguage: String,
        voice: String?
    ): Result<TranslationResult>
    suspend fun translateAudioRaw(
        audioBytes: ByteArray,
        targetLanguage: String,
        voice: String?
    ): Result<ByteArray>
    suspend fun transcribeAudio(audioBytes: ByteArray): Result<TranslationResult>
}
