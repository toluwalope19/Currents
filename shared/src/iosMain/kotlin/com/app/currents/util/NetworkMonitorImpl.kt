package com.app.currents.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NetworkMonitorImpl : NetworkMonitor {
    // iOS — stub for now, always online
    // TODO: replace with NWPathMonitor via expect/actual
    override val isOnline: Flow<Boolean> = flowOf(true)
}