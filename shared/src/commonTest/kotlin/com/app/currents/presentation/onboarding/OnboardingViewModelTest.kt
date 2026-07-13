package com.app.currents.presentation.onboarding

import com.app.currents.data.local.UserPreferences
import com.app.currents.domain.model.Category
import com.app.currents.fakes.FakeDataStore
import com.app.currents.fakes.runViewModelTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OnboardingViewModelTest {

    private fun buildViewModel(dataStore: FakeDataStore = FakeDataStore()) =
        OnboardingViewModel(UserPreferences(dataStore)) to dataStore

    @Test
    fun toggling_a_category_adds_it_to_selection() = runViewModelTest {
        val (viewModel, _) = buildViewModel()

        viewModel.onEvent(OnboardingUiEvent.OnCategoryToggled(Category.Technology))

        assertEquals(listOf(Category.Technology), viewModel.uiState.value.selectedCategories)
    }

    @Test
    fun toggling_the_same_category_twice_removes_it() = runViewModelTest {
        val (viewModel, _) = buildViewModel()

        viewModel.onEvent(OnboardingUiEvent.OnCategoryToggled(Category.Technology))
        viewModel.onEvent(OnboardingUiEvent.OnCategoryToggled(Category.Technology))

        assertEquals(emptyList(), viewModel.uiState.value.selectedCategories)
    }

    @Test
    fun continue_persists_selected_categories_and_completes_onboarding() = runViewModelTest {
        val (viewModel, dataStore) = buildViewModel()
        viewModel.onEvent(OnboardingUiEvent.OnCategoryToggled(Category.Sports))
        viewModel.onEvent(OnboardingUiEvent.OnCategoryToggled(Category.Business))

        viewModel.onEvent(OnboardingUiEvent.OnContinue)
        advanceUntilIdle()

        val prefs = UserPreferences(dataStore)
        assertEquals(setOf("sports", "business"), prefs.selectedCategories.first())
        assertTrue(prefs.isOnboardingComplete.first())
    }

    @Test
    fun skip_completes_onboarding_without_requiring_a_selection() = runViewModelTest {
        val (viewModel, dataStore) = buildViewModel()

        viewModel.onEvent(OnboardingUiEvent.OnSkip)
        advanceUntilIdle()

        assertTrue(UserPreferences(dataStore).isOnboardingComplete.first())
    }
}
