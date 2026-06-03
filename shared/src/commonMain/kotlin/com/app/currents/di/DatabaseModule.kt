package com.app.currents.di

import com.app.currents.data.local.ArticleLocalDataSource
import com.app.currents.data.local.BookmarkLocalDataSource
import com.app.currents.data.local.DatabaseFactory
import org.koin.dsl.module

val databaseModule = module {
    single { get<DatabaseFactory>().create() }
    single { ArticleLocalDataSource(get()) }
    single { BookmarkLocalDataSource(get()) }
}