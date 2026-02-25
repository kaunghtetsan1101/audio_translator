package com.audiotranslator.domain.usecase

import com.audiotranslator.domain.model.Language
import com.audiotranslator.domain.repository.TranslatorRepository

class GetLanguagesUseCase(private val repository: TranslatorRepository) {
    suspend operator fun invoke(): Result<List<Language>> = repository.getLanguages()
}
