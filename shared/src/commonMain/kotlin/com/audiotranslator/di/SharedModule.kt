package com.audiotranslator.di

import com.audiotranslator.data.network.TranslatorApi
import com.audiotranslator.data.repository.TranslatorRepositoryImpl
import com.audiotranslator.domain.repository.TranslatorRepository
import com.audiotranslator.domain.usecase.GetLanguagesUseCase
import com.audiotranslator.domain.usecase.GetVoicesUseCase
import com.audiotranslator.domain.usecase.TranscribeAudioUseCase
import com.audiotranslator.domain.usecase.TranslateAudioUseCase
import org.koin.dsl.module

val networkModule = module {
    single { TranslatorApi() }
}

val repositoryModule = module {
    single<TranslatorRepository> { TranslatorRepositoryImpl(get()) }
}

val domainModule = module {
    factory { GetLanguagesUseCase(get()) }
    factory { GetVoicesUseCase(get()) }
    factory { TranslateAudioUseCase(get()) }
    factory { TranscribeAudioUseCase(get()) }
}

fun commonModules() = listOf(networkModule, repositoryModule, domainModule)
