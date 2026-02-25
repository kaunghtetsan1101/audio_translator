package com.audiotranslator.di

import com.audiotranslator.audio.AudioFilePicker
import com.audiotranslator.audio.AudioPlayer
import com.audiotranslator.audio.AudioRecorder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val audioModule: Module = module {
    single { AudioRecorder() }
    single { AudioPlayer() }
    single { AudioFilePicker() }
}
