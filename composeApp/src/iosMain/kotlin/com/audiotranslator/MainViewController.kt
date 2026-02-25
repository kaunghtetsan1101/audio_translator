package com.audiotranslator

import androidx.compose.ui.window.ComposeUIViewController
import com.audiotranslator.di.audioModule
import com.audiotranslator.di.commonModules
import com.audiotranslator.di.viewModelModule
import org.koin.core.context.startKoin

private var koinStarted = false

fun MainViewController() = run {
    if (!koinStarted) {
        koinStarted = true
        startKoin {
            modules(commonModules() + audioModule + viewModelModule)
        }
    }
    ComposeUIViewController { App() }
}
