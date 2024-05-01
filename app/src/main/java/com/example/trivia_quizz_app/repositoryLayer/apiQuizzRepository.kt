package com.example.trivia_quizz_app.repositoryLayer

import com.example.trivia_quizz_app.dataLayer.Api
import com.example.trivia_quizz_app.dataLayer.model.ApiQuizz

class ApiQuizzRepository(
    private val api: Api
) {
    suspend fun getQuizz(category: Int): ApiQuizz {
        return api.getQuizz(category)
    }
}