package com.example.trivia_quizz_app.dataLayer.entities

data class ApiQuestionAndAnswers(
    val category: String,
    val correct_answer: String,
    val difficulty: String,
    val incorrect_answers: List<String>,
    val question: String,
    val type: String
)