package com.audiotranslator.domain.usecase

import com.audiotranslator.domain.model.TranslationResult
import com.audiotranslator.domain.repository.TranslatorRepository

class TranslateAudioUseCase(private val repository: TranslatorRepository) {
    suspend operator fun invoke(
        audioBytes: ByteArray,
        targetLanguage: String,
        voice: String?
    ): Result<Pair<TranslationResult, ByteArray>> {
        val textResult = repository.translateAudio(audioBytes, targetLanguage, voice)
            .getOrElse { return Result.failure(it) }
        val audioResult = repository.translateAudioRaw(audioBytes, targetLanguage, voice)
            .getOrElse { return Result.failure(it) }
        return Result.success(Pair(textResult, audioResult))
    }
}
