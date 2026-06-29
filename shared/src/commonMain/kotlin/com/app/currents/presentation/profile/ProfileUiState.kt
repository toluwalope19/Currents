package com.app.currents.presentation.profile

import com.app.currents.domain.model.Category
import com.app.currents.presentation.base.UiEffect
import com.app.currents.presentation.base.UiEvent
import com.app.currents.presentation.base.UiState


data class ProfileUiState(
    val name: String = "Alex Rivera",
    val email: String = "alex.rivera@email.com",
    val following: Int = 48,
    val saved: Int = 12,
    val dayStreak: Int = 7,
    val interests: List<Category> = listOf(
        Category.Technology, Category.Sports, Category.Health,
        Category.World, Category.Business,
    ),
    val notificationsEnabled: Boolean = true,
    val isDarkTheme: Boolean = true,
) : UiState

sealed interface ProfileUiEvent : UiEvent {
    data object OnEditProfile : ProfileUiEvent
    data object OnToggleNotifications : ProfileUiEvent
    data class OnThemeToggle(val isDark: Boolean) : ProfileUiEvent
    data object OnManageInterests : ProfileUiEvent
    data object OnReadingHistory : ProfileUiEvent
    data object OnSavedForOffline : ProfileUiEvent
    data object OnTextSize : ProfileUiEvent
    data object OnRegionLanguage : ProfileUiEvent
    data object OnPrivacyData : ProfileUiEvent
    data object OnHelpSupport : ProfileUiEvent
    data object OnSignOut : ProfileUiEvent
    data object OnUpgrade : ProfileUiEvent
    data object OnSettings : ProfileUiEvent
}

sealed interface ProfileUiEffect : UiEffect {
    data object NavigateToOnboarding : ProfileUiEffect
    data object ShowEditProfile : ProfileUiEffect
    data object ShowComingSoon : ProfileUiEffect
}