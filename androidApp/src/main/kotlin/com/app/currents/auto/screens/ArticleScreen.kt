package com.app.currents.auto.screens

import android.speech.tts.TextToSpeech
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import com.app.currents.domain.model.Article
import java.util.Locale

class ArticleScreen(
    carContext: CarContext,
    private val article: Article,
) : Screen(carContext) {

    private var tts: TextToSpeech? = null
    private var isSpeaking = false

    init {
        tts = TextToSpeech(carContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }
        lifecycle.addObserver(object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onDestroy(owner: androidx.lifecycle.LifecycleOwner) {
                tts?.stop()
                tts?.shutdown()
                tts = null
            }
        })
    }

    override fun onGetTemplate(): Template {
        val readAction = Action.Builder()
            .setTitle(if (isSpeaking) "Stop" else "Read aloud")
            .setOnClickListener {
                if (isSpeaking) {
                    tts?.stop()
                    isSpeaking = false
                } else {
                    val text = "${article.title}. ${article.description}"
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, article.id)
                    isSpeaking = true
                }
                invalidate()
            }
            .build()

        return LongMessageTemplate.Builder(
            "${article.title}\n\n${article.description}"
        )
            .setTitle(article.title.take(50))
            .setHeaderAction(Action.BACK)
            .addAction(readAction)
            .build()
    }

}