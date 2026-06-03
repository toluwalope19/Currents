package com.app.currents.di


import com.app.currents.data.remote.api.NewsDataApi
import com.app.currents.data.remote.api.NewsDataApiImpl
import com.app.currents.data.remote.api.createHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient() }
    single<NewsDataApi> {
        NewsDataApiImpl(
            client = get(),
            apiKey = get(named("newsApiKey")),
        )
    }
}