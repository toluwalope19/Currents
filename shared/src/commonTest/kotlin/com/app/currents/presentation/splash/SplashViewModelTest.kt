package com.app.currents.presentation.splash

import com.app.currents.data.local.UserPreferences
import com.app.currents.fakes.FakeDataStore
import com.app.currents.fakes.runViewModelTest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SplashViewModelTest {

    @Test
    fun isOnboardingComplete_is_null_until_datastore_emits() = runViewModelTest {
        val dataStore = FakeDataStore()
        val viewModel = SplashViewModel(UserPreferences(dataStore))

        assertNull(viewModel.isOnboardingComplete.value)
    }

    @Test
    fun isOnboardingComplete_reflects_stored_preference() = runViewModelTest {
        val dataStore = FakeDataStore()
        val preferences = UserPreferences(dataStore)
        preferences.setOnboardingComplete()
        val viewModel = SplashViewModel(preferences)

        // `stateIn(started = WhileSubscribed(...))` only starts collecting the upstream once
        // someone subscribes to the shared StateFlow — reading `.value` alone won't trigger it.
        backgroundScope.launch { viewModel.isOnboardingComplete.collect {} }
        advanceUntilIdle()

        assertEquals(true, viewModel.isOnboardingComplete.value)
    }
}
