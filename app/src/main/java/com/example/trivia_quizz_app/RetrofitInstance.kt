package com.example.trivia_quizz_app

import com.example.trivia_quizz_app.dataLayer.QuickChartApi
import com.example.trivia_quizz_app.dataLayer.TriviaApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val triviaApi: TriviaApi = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(TriviaApi.TRIVIA_BASE_URL)
        .client(client)
        .build()
        .create(TriviaApi::class.java)

    val quickChartApi: QuickChartApi = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(QuickChartApi.QUICKCHART_BASE_URL)
        .client(client)
        .build()
        .create(QuickChartApi::class.java)
}