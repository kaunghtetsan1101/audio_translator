package com.audiotranslator

import android.app.Application
import com.audiotranslator.di.audioModule
import com.audiotranslator.di.commonModules
import com.audiotranslator.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AudioTranslatorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AudioTranslatorApp)
            modules(commonModules() + audioModule + viewModelModule)
        }
    }
}
