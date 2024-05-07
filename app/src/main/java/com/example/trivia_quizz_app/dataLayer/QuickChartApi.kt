package com.example.trivia_quizz_app.dataLayer

import retrofit2.http.GET
import retrofit2.http.Query

interface QuickChartApi {
    @GET("chart")
    suspend fun getGraph(
        @Query("c") graphStructure: String,
    )

    companion object {
        const val QUICKCHART_BASE_URL = "https://quickchart.io/"
    }
}