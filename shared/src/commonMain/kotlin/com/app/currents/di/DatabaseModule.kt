package com.app.currents.di

import com.app.currents.data.local.ArticleLocalDataSource
import com.app.currents.data.local.BookmarkLocalDataSource
import com.app.currents.data.local.DataStoreFactory
import com.app.currents.data.local.DatabaseFactory
import com.app.currents.data.local.UserPreferences
import org.koin.dsl.module

val databaseModule = module {
    single { get<DatabaseFactory>().create() }
    single { ArticleLocalDataSource(get()) }
    single { BookmarkLocalDataSource(get()) }
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}