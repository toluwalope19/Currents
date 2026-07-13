package com.app.currents.auto.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import com.app.currents.domain.model.Article
import com.app.currents.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class HeadlinesScreen(carContext: CarContext) : Screen(carContext) {

    private val getFeed: GetFeedUseCase by inject(GetFeedUseCase::class.java)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var articles: List<Article> = emptyList()
    private var isLoading = true

    init {
        loadFeed()
        lifecycle.addObserver(object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onDestroy(owner: androidx.lifecycle.LifecycleOwner) {
                scope.cancel()
            }
        })
    }

    private fun loadFeed() {
        scope.launch {
            getFeed()
                .take(1)
                .collect { result ->
                    articles = result
                    isLoading = false
                    invalidate()  // tells car OS to re-render the template
                }
        }
    }

    override fun onGetTemplate(): Template {
        if (isLoading) {
            return ListTemplate.Builder()
                .setTitle("Currents")
                .setLoading(true)
                .build()
        }

        val itemList = ItemList.Builder()

        articles.take(6).forEach { article ->  // car OS limits list items
            itemList.addItem(
                Row.Builder()
                    .setTitle(article.title.take(60))
                    .addText(article.source)
                    .setOnClickListener {
                        screenManager.push(ArticleScreen(carContext, article))
                    }
                    .build()
            )
        }

        return ListTemplate.Builder()
            .setTitle("⚡ Currents")
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(itemList.build())
            .build()
    }

}