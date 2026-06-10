package com.app.currents.presentation.onboarding

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currents.data.local.UserPreferences
import com.app.currents.domain.model.Category
import com.app.currents.presentation.base.UiEvent
import com.app.currents.presentation.base.UiState
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val selectedCategories: List<Category> = emptyList(),
) : UiState

sealed interface OnboardingUiEvent : UiEvent {
    data class OnCategoryToggled(val category: Category) : OnboardingUiEvent
    data object OnContinue : OnboardingUiEvent
    data object OnSkip : OnboardingUiEvent
}

class OnboardingViewModel(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = mutableStateOf(OnboardingUiState())
    val uiState: State<OnboardingUiState> = _uiState

    fun onEvent(event: OnboardingUiEvent) {
        when (event) {
            is OnboardingUiEvent.OnCategoryToggled -> toggleCategory(event.category)
            OnboardingUiEvent.OnContinue -> completeOnboarding()
            OnboardingUiEvent.OnSkip -> completeOnboarding()
        }
    }

    private fun toggleCategory(category: Category) {
        val current = _uiState.value.selectedCategories
        val updated = if (current.contains(category)) {
            current - category
        } else {
            current + category
        }
        _uiState.value = _uiState.value.copy(selectedCategories = updated)
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.saveSelectedCategories(
                _uiState.value.selectedCategories
                    .map { it.apiValue }
                    .toSet()
            )
            userPreferences.setOnboardingComplete()
        }
    }
}