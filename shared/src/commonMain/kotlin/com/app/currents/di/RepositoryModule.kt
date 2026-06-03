package com.app.currents.di

import com.app.currents.data.repository.ArticleLocalRepositoryImpl
import com.app.currents.data.repository.ArticleRemoteRepositoryImpl
import com.app.currents.data.repository.BookmarkRepositoryImpl
import com.app.currents.domain.repository.ArticleLocalRepository
import com.app.currents.domain.repository.ArticleRemoteRepository
import com.app.currents.domain.repository.BookmarkRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ArticleRemoteRepository> { ArticleRemoteRepositoryImpl(get()) }
    single<ArticleLocalRepository> { ArticleLocalRepositoryImpl(get()) }
    single<BookmarkRepository> { BookmarkRepositoryImpl(get()) }
}