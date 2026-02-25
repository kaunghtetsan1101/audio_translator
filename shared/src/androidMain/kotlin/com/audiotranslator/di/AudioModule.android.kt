package com.audiotranslator.di

import com.audiotranslator.audio.AudioFilePicker
import com.audiotranslator.audio.AudioPlayer
import com.audiotranslator.audio.AudioRecorder
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val audioModule: Module = module {
    single { AudioRecorder(androidContext()) }
    single { AudioPlayer(androidContext()) }
    single { AudioFilePicker(androidContext()) }
}
