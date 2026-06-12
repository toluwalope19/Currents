package com.app.currents.di

import com.app.currents.domain.usecase.AddBookmarkUseCase
import com.app.currents.domain.usecase.GetByCategoryUseCase
import com.app.currents.domain.usecase.GetBookmarksUseCase
import com.app.currents.domain.usecase.GetFeedUseCase
import com.app.currents.domain.usecase.HasCachedArticlesUseCase
import com.app.currents.domain.usecase.IsBookmarkedUseCase
import com.app.currents.domain.usecase.RefreshFeedUseCase
import com.app.currents.domain.usecase.RemoveBookmarkUseCase
import com.app.currents.domain.usecase.SearchArticlesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetFeedUseCase(get(), get()) }
    factory { GetByCategoryUseCase(get(), get()) }
    factory { SearchArticlesUseCase(get()) }
    factory { RefreshFeedUseCase(get(), get()) }
    factory { GetBookmarksUseCase(get()) }
    factory { AddBookmarkUseCase(get()) }
    factory { RemoveBookmarkUseCase(get()) }
    factory { IsBookmarkedUseCase(get()) }
    factory { HasCachedArticlesUseCase(get()) }
}