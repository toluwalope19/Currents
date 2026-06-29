package com.app.currents.presentation.profile

import com.app.currents.domain.model.Category
import com.app.currents.domain.usecase.GetBookmarksUseCase
import com.app.currents.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class ProfileViewModel(
) : BaseViewModel<ProfileUiState, ProfileUiEvent, ProfileUiEffect>(
    initialState = ProfileUiState(),
) {
    override fun onEvent(event: ProfileUiEvent) {
        when (event) {
            ProfileUiEvent.OnToggleNotifications ->
                setState { copy(notificationsEnabled = !notificationsEnabled) }
            is ProfileUiEvent.OnThemeToggle ->
                setState { copy(isDarkTheme = event.isDark) }
            ProfileUiEvent.OnSignOut ->
                sendEffect(ProfileUiEffect.NavigateToOnboarding)
            ProfileUiEvent.OnEditProfile ->
                sendEffect(ProfileUiEffect.ShowEditProfile)
            // All others are stubs for now
            else -> sendEffect(ProfileUiEffect.ShowComingSoon)
        }
    }
}