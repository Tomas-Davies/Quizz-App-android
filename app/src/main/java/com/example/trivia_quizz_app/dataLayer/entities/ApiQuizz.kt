package com.example.trivia_quizz_app.dataLayer.entities


data class ApiQuizz(
    val response_code: Int,
    val results: List<ApiQuestionAndAnswers>
)