package com.app.currents

import androidx.compose.ui.window.ComposeUIViewController
import com.app.currents.data.local.DatabaseFactory
import com.app.currents.data.local.DataStoreFactory
import com.app.currents.di.AppConfig
import com.app.currents.di.appModules
import com.app.currents.ui.App
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun MainViewController(newsApiKey: String) = run {
    startKoin {
        modules(
            module {
                single { AppConfig(newsApiKey = newsApiKey) }
                single(named("newsApiKey")) { newsApiKey }
                single { DatabaseFactory() }
                single { DataStoreFactory() }
            },
            *appModules.toTypedArray(),
        )
    }
    ComposeUIViewController { App() }
}
