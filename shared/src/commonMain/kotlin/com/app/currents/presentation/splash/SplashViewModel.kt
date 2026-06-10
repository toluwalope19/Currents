package com.app.currents.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currents.data.local.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SplashViewModel(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    val isOnboardingComplete = userPreferences.isOnboardingComplete
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
}