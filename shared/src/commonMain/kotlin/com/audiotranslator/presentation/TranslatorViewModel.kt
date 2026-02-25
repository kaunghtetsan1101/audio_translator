package com.audiotranslator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audiotranslator.audio.AudioFilePicker
import com.audiotranslator.audio.AudioPlayer
import com.audiotranslator.audio.AudioPlayerCallback
import com.audiotranslator.audio.AudioRecorder
import com.audiotranslator.audio.AudioRecorderCallback
import com.audiotranslator.domain.model.Language
import com.audiotranslator.domain.model.Voice
import com.audiotranslator.domain.usecase.GetLanguagesUseCase
import com.audiotranslator.domain.usecase.GetVoicesUseCase
import com.audiotranslator.domain.model.TranslationMode
import com.audiotranslator.domain.usecase.TranscribeAudioUseCase
import com.audiotranslator.domain.usecase.TranslateAudioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TranslatorViewModel(
    private val getLanguages: GetLanguagesUseCase,
    private val getVoices: GetVoicesUseCase,
    private val translateAudio: TranslateAudioUseCase,
    private val transcribeAudio: TranscribeAudioUseCase,
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer,
    private val audioFilePicker: AudioFilePicker
) : ViewModel() {

    private val _state = MutableStateFlow(TranslatorState())
    val state: StateFlow<TranslatorState> = _state.asStateFlow()

    init {
        setupRecorderCallbacks()
        setupPlayerCallbacks()
        loadLanguages()
    }

    private fun setupRecorderCallbacks() {
        audioRecorder.callback = object : AudioRecorderCallback {
            override fun onRecordingStarted() {
                _state.update { it.copy(isRecording = true) }
            }

            override fun onRecordingStopped(audioBytes: ByteArray) {
                _state.update {
                    it.copy(
                        isRecording = false,
                        recordedAudioBytes = audioBytes.toList(),
                        pickedFileName = null,
                        translationResult = null,
                        translatedAudioBytes = null
                    )
                }
            }

            override fun onError(message: String) {
                _state.update { it.copy(isRecording = false, errorMessage = message) }
            }
        }
    }

    private fun setupPlayerCallbacks() {
        audioPlayer.callback = object : AudioPlayerCallback {
            override fun onPlaybackStarted() {
                _state.update { it.copy(isPlayingAudio = true) }
            }

            override fun onPlaybackCompleted() {
                _state.update { it.copy(isPlayingAudio = false) }
            }

            override fun onError(message: String) {
                _state.update { it.copy(isPlayingAudio = false, errorMessage = message) }
            }
        }
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingLanguages = true) }
            getLanguages().fold(
                onSuccess = { languages ->
                    _state.update {
                        it.copy(
                            isLoadingLanguages = false,
                            languages = languages,
                            selectedLanguage = languages.firstOrNull()
                        )
                    }
                    languages.firstOrNull()?.let { lang -> loadVoices(lang.code) }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoadingLanguages = false,
                            errorMessage = "Failed to load languages: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    private fun loadVoices(langCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingVoices = true, voices = emptyList(), selectedVoice = null) }
            getVoices(langCode).fold(
                onSuccess = { voices ->
                    _state.update {
                        it.copy(
                            isLoadingVoices = false,
                            voices = voices,
                            selectedVoice = voices.firstOrNull()
                        )
                    }
                },
                onFailure = { _ ->
                    _state.update { it.copy(isLoadingVoices = false) }
                }
            )
        }
    }

    fun onLanguageSelected(language: Language) {
        _state.update { it.copy(selectedLanguage = language) }
        loadVoices(language.code)
    }

    fun onVoiceSelected(voice: Voice) {
        _state.update { it.copy(selectedVoice = voice) }
    }

    fun onModeChanged(mode: TranslationMode) {
        _state.update { it.copy(mode = mode, translationResult = null, translatedAudioBytes = null) }
    }

    fun onCancelRecording() {
        audioRecorder.cancelRecording()
        _state.update { it.copy(isRecording = false) }
    }

    fun onClearRecording() {
        _state.update {
            it.copy(
                recordedAudioBytes = null,
                pickedFileName = null,
                translationResult = null,
                translatedAudioBytes = null
            )
        }
    }

    fun onClearResult() {
        _state.update { it.copy(translationResult = null, translatedAudioBytes = null) }
    }

    fun onToggleRecording() {
        if (_state.value.isRecording) {
            audioRecorder.stopRecording()
        } else {
            _state.update {
                it.copy(
                    translationResult = null,
                    translatedAudioBytes = null,
                    errorMessage = null
                )
            }
            audioRecorder.startRecording()
        }
    }

    fun onPickAudioFile() {
        audioFilePicker.pickAudioFile { bytes ->
            if (bytes != null) {
                _state.update {
                    it.copy(
                        recordedAudioBytes = bytes.toList(),
                        pickedFileName = "Selected audio file",
                        translationResult = null,
                        translatedAudioBytes = null,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun onTranslate() {
        val currentState = _state.value
        val audioBytes = currentState.recordedAudioBytes ?: return

        viewModelScope.launch {
            _state.update { it.copy(isTranslating = true, errorMessage = null) }
            if (currentState.mode == TranslationMode.TRANSCRIBE) {
                transcribeAudio(audioBytes.toByteArray()).fold(
                    onSuccess = { result ->
                        _state.update {
                            it.copy(
                                isTranslating = false,
                                translationResult = result,
                                translatedAudioBytes = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isTranslating = false,
                                errorMessage = "Transcription failed: ${error.message}"
                            )
                        }
                    }
                )
            } else {
                val targetLanguage = currentState.selectedLanguage?.code ?: run {
                    _state.update { it.copy(isTranslating = false) }
                    return@launch
                }
                translateAudio(
                    audioBytes = audioBytes.toByteArray(),
                    targetLanguage = targetLanguage,
                    voice = currentState.selectedVoice?.id
                ).fold(
                    onSuccess = { (textResult, audioBytes) ->
                        _state.update {
                            it.copy(
                                isTranslating = false,
                                translationResult = textResult,
                                translatedAudioBytes = audioBytes.toList()
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isTranslating = false,
                                errorMessage = "Translation failed: ${error.message}"
                            )
                        }
                    }
                )
            }
        }
    }

    fun onPlayTranslatedAudio() {
        val bytes = _state.value.translatedAudioBytes ?: return
        if (_state.value.isPlayingAudio) {
            audioPlayer.stop()
        } else {
            audioPlayer.play(bytes.toByteArray())
        }
    }

    fun onDismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
