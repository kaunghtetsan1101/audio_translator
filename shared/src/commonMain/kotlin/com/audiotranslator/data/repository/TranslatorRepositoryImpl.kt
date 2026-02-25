package com.audiotranslator.data.repository

import com.audiotranslator.data.network.TranslatorApi
import com.audiotranslator.domain.model.Language
import com.audiotranslator.domain.model.TranslationResult
import com.audiotranslator.domain.model.Voice
import com.audiotranslator.domain.repository.TranslatorRepository

class TranslatorRepositoryImpl(
    private val api: TranslatorApi
) : TranslatorRepository {

    override suspend fun getLanguages(): Result<List<Language>> = runCatching {
        api.getLanguages().map { dto ->
            Language(code = dto.code, name = dto.name)
        }
    }

    override suspend fun getVoices(langCode: String): Result<List<Voice>> = runCatching {
        api.getVoices(langCode).map { dto ->
            Voice(id = dto.id, name = dto.name, gender = dto.gender)
        }
    }

    override suspend fun translateAudio(
        audioBytes: ByteArray,
        targetLanguage: String,
        voice: String?
    ): Result<TranslationResult> = runCatching {
        val dto = api.translateAudio(
            audioBytes = audioBytes,
            targetLanguage = targetLanguage,
            voice = voice
        )
        TranslationResult(
            transcription = dto.transcription,
            translation = dto.translation,
            detectedLanguage = dto.detectedLanguage,
            targetLanguage = dto.targetLanguage ?: targetLanguage
        )
    }

    override suspend fun translateAudioRaw(
        audioBytes: ByteArray,
        targetLanguage: String,
        voice: String?
    ): Result<ByteArray> = runCatching {
        api.translateAudioRaw(
            audioBytes = audioBytes,
            targetLanguage = targetLanguage,
            voice = voice
        )
    }

    override suspend fun transcribeAudio(audioBytes: ByteArray): Result<TranslationResult> = runCatching {
        val dto = api.transcribeAudio(audioBytes)
        TranslationResult(
            transcription = dto.transcription,
            detectedLanguage = dto.detectedLanguage
        )
    }
}
