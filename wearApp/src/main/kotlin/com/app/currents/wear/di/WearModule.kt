package com.app.currents.wear.di

import android.speech.tts.TextToSpeech
import com.app.currents.wear.presentation.article.WearArticleViewModel
import com.app.currents.wear.presentation.feed.FeedViewModel
import com.app.currents.wear.presentation.saved.SavedViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.Locale

val wearModule = module {
    single {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(androidContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.getDefault()
            }
        }
        ttsInstance
    }
    viewModel { FeedViewModel(get()) }
    viewModel {
        WearArticleViewModel(
            tts = get(),
            getArticleById = get(),
            addBookmark = get(),
            removeBookmark = get(),
            isBookmarked = get(),
        )
    }
    viewModel { SavedViewModel(get()) }
}