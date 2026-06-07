package com.app.currents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import com.app.currents.data.local.DataStoreFactory
import com.app.currents.di.AppConfig
import com.app.currents.di.appModules
import com.app.currents.ui.App
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startKoin {
            androidContext(this@MainActivity)
            modules(
                module {
                    single { AppConfig(newsApiKey = BuildConfig.NEWS_API_KEY) }
                    single(named("newsApiKey")) { BuildConfig.NEWS_API_KEY }
                    single { com.app.currents.data.local.DatabaseFactory(androidContext()) }
                    single { DataStoreFactory(androidContext()) }
                },
                *appModules.toTypedArray(),
            )
        }

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                App()
            }
        }
    }
}