package com.example.trivia_quizz_app.repositoryLayer

import com.example.trivia_quizz_app.dataLayer.TriviaApi
import com.example.trivia_quizz_app.dataLayer.model.ApiQuizz

class ApiQuizzRepository(
    private val api: TriviaApi
) {
    suspend fun getQuizz(category: Int): ApiQuizz {
        return api.getQuizz(category)
    }
}