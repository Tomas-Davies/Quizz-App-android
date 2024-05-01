package com.example.trivia_quizz_app.dataLayer.model

data class ApiQuizz(
    val response_code: Int,
    val apiQuestionAndAnswers: List<ApiQuestionAndAnswers>
)