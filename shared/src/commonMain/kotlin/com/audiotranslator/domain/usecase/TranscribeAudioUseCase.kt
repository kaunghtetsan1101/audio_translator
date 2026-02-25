package com.audiotranslator.domain.usecase

import com.audiotranslator.domain.model.TranslationResult
import com.audiotranslator.domain.repository.TranslatorRepository

class TranscribeAudioUseCase(private val repository: TranslatorRepository) {
    suspend operator fun invoke(audioBytes: ByteArray): Result<TranslationResult> =
        repository.transcribeAudio(audioBytes)
}
