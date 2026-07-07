package com.app.currents.di

import com.app.currents.util.NetworkMonitor
import com.app.currents.util.NetworkMonitorImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<NetworkMonitor> { NetworkMonitorImpl() }
}