package com.app.currents.fakes

import com.app.currents.util.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNetworkMonitor(online: Boolean = true) : NetworkMonitor {
    private val _isOnline = MutableStateFlow(online)
    override val isOnline = _isOnline

    fun setOnline(value: Boolean) {
        _isOnline.value = value
    }
}
