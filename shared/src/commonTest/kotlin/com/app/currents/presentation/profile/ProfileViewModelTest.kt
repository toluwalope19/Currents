package com.app.currents.presentation.profile

import com.app.currents.fakes.runViewModelTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ProfileViewModelTest {

    @Test
    fun toggle_notifications_flips_the_flag() = runViewModelTest {
        val viewModel = ProfileViewModel()
        val initial = viewModel.uiState.value.notificationsEnabled

        viewModel.onEvent(ProfileUiEvent.OnToggleNotifications)
        advanceUntilIdle()

        assertEquals(!initial, viewModel.uiState.value.notificationsEnabled)
    }

    @Test
    fun theme_toggle_sets_isDarkTheme() = runViewModelTest {
        val viewModel = ProfileViewModel()

        viewModel.onEvent(ProfileUiEvent.OnThemeToggle(isDark = false))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isDarkTheme)
    }

    @Test
    fun sign_out_sends_navigate_to_onboarding_effect() = runViewModelTest {
        val viewModel = ProfileViewModel()

        viewModel.onEvent(ProfileUiEvent.OnSignOut)
        advanceUntilIdle()

        assertEquals(ProfileUiEffect.NavigateToOnboarding, viewModel.effect.first())
    }

    @Test
    fun unimplemented_events_send_coming_soon_effect() = runViewModelTest {
        val viewModel = ProfileViewModel()

        viewModel.onEvent(ProfileUiEvent.OnUpgrade)
        advanceUntilIdle()

        assertEquals(ProfileUiEffect.ShowComingSoon, viewModel.effect.first())
    }
}
