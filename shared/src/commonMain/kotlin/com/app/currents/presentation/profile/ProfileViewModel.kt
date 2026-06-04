package com.app.currents.presentation.profile

import com.app.currents.domain.model.Category
import com.app.currents.domain.usecase.GetBookmarksUseCase
import com.app.currents.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProfileViewModel(
    private val getBookmarksUseCase: GetBookmarksUseCase,
) : BaseViewModel<ProfileUiState, ProfileUiEvent, ProfileUiEffect>(ProfileUiState()) {

    init {
        observeBookmarksCount()
    }

    override fun onEvent(event: ProfileUiEvent) {
        when (event) {
            ProfileUiEvent.OnDarkModeToggled -> {
                setState { copy(isDarkMode = !isDarkMode) }
            }
            is ProfileUiEvent.OnCategoryToggled -> toggleCategory(event.category)
            ProfileUiEvent.OnClearBookmarks -> {
                sendEffect(ProfileUiEffect.ShowClearBookmarksConfirmation)
            }
            ProfileUiEvent.OnAboutClicked -> {
                sendEffect(ProfileUiEffect.NavigateToAbout)
            }
        }
    }

    private fun toggleCategory(category: Category) {
        setState {
            val updated = if (selectedCategories.contains(category)) {
                selectedCategories - category
            } else {
                selectedCategories + category
            }
            copy(selectedCategories = updated)
        }
    }

    private fun observeBookmarksCount() {
        getBookmarksUseCase()
            .onEach { bookmarks ->
                setState { copy(bookmarksCount = bookmarks.size) }
            }
            .catch { }
            .launchIn(viewModelScope)
    }
}