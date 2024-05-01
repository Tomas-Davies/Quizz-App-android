package com.example.trivia_quizz_app.dataLayer

import com.example.trivia_quizz_app.dataLayer.model.ApiQuizz
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("api.php")
    suspend fun getQuizz(
        @Query("category") category: Int,
        @Query("type") type: String = "multiple",
        @Query("ammount") ammount: Int = 10
    ): ApiQuizz

    companion object {
        const val BASE_URL = "https://opentdb.com/"
    }
}