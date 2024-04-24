package com.example.trivia_quizz_app

import android.app.Application
import com.example.trivia_quizz_app.dataLayer.AppDatabase
import com.example.trivia_quizz_app.repositoryLayer.QuizzRepository

class QuizzApp: Application() {
    val db by lazy { AppDatabase.getDatabase(this) }
    val quizzRepo by lazy { QuizzRepository(db.quizzDao()) }
}