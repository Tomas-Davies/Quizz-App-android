package com.example.trivia_quizz_app

import android.app.Application
import com.example.trivia_quizz_app.dataLayer.AppDatabase
import com.example.trivia_quizz_app.dataLayer.SharedPreferencesManager
import com.example.trivia_quizz_app.repositoryLayer.QuizzRepository

class QuizzApp: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(this)
    }

    val db by lazy { AppDatabase.getDatabase(this) }
    val quizzRepo by lazy { QuizzRepository(db.quizzDao()) }
}