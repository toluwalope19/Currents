package com.app.currents.auto

import androidx.car.app.Screen
import androidx.car.app.Session
import com.app.currents.auto.screens.HeadlinesScreen

class CurrentsSession : Session() {
    override fun onCreateScreen(intent: android.content.Intent): Screen {
        return HeadlinesScreen(carContext)
    }
}