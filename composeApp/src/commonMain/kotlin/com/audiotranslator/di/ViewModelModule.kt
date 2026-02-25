package com.audiotranslator.di

import com.audiotranslator.presentation.TranslatorViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single {
        TranslatorViewModel(
            getLanguages = get(),
            getVoices = get(),
            translateAudio = get(),
            transcribeAudio = get(),
            audioRecorder = get(),
            audioPlayer = get(),
            audioFilePicker = get()
        )
    }
}
