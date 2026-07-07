package com.app.currents.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.currents.data.local.DatabaseFactory
import com.app.currents.data.local.DataStoreFactory
import com.app.currents.di.appModules
import com.app.currents.di.platformModule
import com.app.currents.wear.di.wearModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@MainActivity)
                modules(
                    module {
                        single { DatabaseFactory(androidContext()) }
                        single { DataStoreFactory(androidContext()) }
                    },
                    platformModule,
                    *appModules.toTypedArray(),
                    wearModule,
                )
            }
        }

        setContent {
            WearApp()
        }
    }
}