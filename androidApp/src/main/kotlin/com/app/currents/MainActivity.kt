package com.app.currents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.graphics.Color
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
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import org.koin.android.ext.android.get
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@MainActivity)
                modules(
                    module {
                        single { AppConfig(newsApiKey = BuildConfig.NEWS_API_KEY,  claudeApiKey = BuildConfig.CLAUDE_API_KEY,) }
                        single(named("newsApiKey")) { BuildConfig.NEWS_API_KEY }
                        single { com.app.currents.data.local.DatabaseFactory(androidContext()) }
                        single { DataStoreFactory(androidContext()) }
                    },
                    *appModules.toTypedArray(),
                )
            }
        }

        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components {
                    add(KtorNetworkFetcherFactory(get<HttpClient>()))
                }
                .build()
        }

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                App()
            }
        }
    }
}