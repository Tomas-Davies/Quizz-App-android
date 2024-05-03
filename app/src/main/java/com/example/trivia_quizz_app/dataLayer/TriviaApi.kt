package com.example.trivia_quizz_app.dataLayer

import com.example.trivia_quizz_app.dataLayer.model.ApiQuizz
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {
    @GET("api.php")
    suspend fun getQuizz(
        @Query("category") category: Int,
        @Query("type") type: String = "multiple",
        @Query("amount") amount: Int = 10,
        @Query("difficulty") difficulty: String = "easy"
    ): ApiQuizz

    companion object {
        const val TRIVIA_BASE_URL = "https://opentdb.com/"
    }
}