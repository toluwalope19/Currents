package com.app.currents.di

import com.app.currents.presentation.article.ArticleViewModel
import com.app.currents.presentation.bookmarks.BookmarksViewModel
import com.app.currents.presentation.explore.ExploreViewModel
import com.app.currents.presentation.home.HomeViewModel
import com.app.currents.presentation.onboarding.OnboardingViewModel
import com.app.currents.presentation.profile.ProfileViewModel
import com.app.currents.presentation.search.SearchViewModel
import com.app.currents.presentation.splash.SplashViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            getFeedUseCase = get(),
            getByCategoryUseCase = get(),
            refreshFeedUseCase = get(),
            hasCachedArticlesUseCase = get(),
            networkMonitor = get()
        )
    }
    viewModel {
        SplashViewModel(userPreferences = get())
    }
    viewModel {
        ExploreViewModel(
            getByCategoryUseCase = get(), networkMonitor = get()
        )
    }
    viewModel {
        OnboardingViewModel(userPreferences = get())
    }
    viewModel {
        ArticleViewModel(
            addBookmarkUseCase = get(),
            removeBookmarkUseCase = get(),
            isBookmarkedUseCase = get(),
            getArticleByIdUseCase = get (),
            appConfig = get(),
        )
    }
    viewModel {
        SearchViewModel(
            searchArticles = get(),
            getFeed = get()
        )
    }
    viewModel {
        BookmarksViewModel(
            getBookmarks = get(),
            removeBookmark = get(),
        )
    }
    viewModel {
        ProfileViewModel(
        )
    }
}