package com.audiotranslator.domain.usecase

import com.audiotranslator.domain.model.Voice
import com.audiotranslator.domain.repository.TranslatorRepository

class GetVoicesUseCase(private val repository: TranslatorRepository) {
    suspend operator fun invoke(langCode: String): Result<List<Voice>> =
        repository.getVoices(langCode)
}
