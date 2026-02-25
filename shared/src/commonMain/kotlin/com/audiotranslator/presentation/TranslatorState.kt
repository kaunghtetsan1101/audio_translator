package com.audiotranslator.presentation

import com.audiotranslator.domain.model.Language
import com.audiotranslator.domain.model.TranslationMode
import com.audiotranslator.domain.model.TranslationResult
import com.audiotranslator.domain.model.Voice

data class TranslatorState(
    val languages: List<Language> = emptyList(),
    val isLoadingLanguages: Boolean = false,
    val voices: List<Voice> = emptyList(),
    val isLoadingVoices: Boolean = false,
    val selectedLanguage: Language? = null,
    val selectedVoice: Voice? = null,
    val isRecording: Boolean = false,
    val recordedAudioBytes: List<Byte>? = null,
    val pickedFileName: String? = null,
    val isTranslating: Boolean = false,
    val translationResult: TranslationResult? = null,
    val translatedAudioBytes: List<Byte>? = null,
    val isPlayingAudio: Boolean = false,
    val errorMessage: String? = null,
    val mode: TranslationMode = TranslationMode.TRANSLATE
) {
    val hasAudioInput: Boolean get() = recordedAudioBytes != null
    val canTranslate: Boolean get() = hasAudioInput && !isTranslating &&
            (mode == TranslationMode.TRANSCRIBE || selectedLanguage != null)

}
