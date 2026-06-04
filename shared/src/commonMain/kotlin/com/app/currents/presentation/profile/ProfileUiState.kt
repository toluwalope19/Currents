package com.app.currents.presentation.profile

import com.app.currents.domain.model.Category
import com.app.currents.presentation.base.UiEffect
import com.app.currents.presentation.base.UiEvent
import com.app.currents.presentation.base.UiState

data class ProfileUiState(
    val isDarkMode: Boolean = true,
    val selectedCategories: List<Category> = emptyList(),
    val articlesRead: Int = 0,
    val bookmarksCount: Int = 0,
    val appVersion: String = "1.0.0",
) : UiState

sealed interface ProfileUiEvent : UiEvent {
    data object OnDarkModeToggled : ProfileUiEvent
    data class OnCategoryToggled(val category: Category) : ProfileUiEvent
    data object OnClearBookmarks : ProfileUiEvent
    data object OnAboutClicked : ProfileUiEvent
}

sealed interface ProfileUiEffect : UiEffect {
    data object ShowClearBookmarksConfirmation : ProfileUiEffect
    data object NavigateToAbout : ProfileUiEffect
}