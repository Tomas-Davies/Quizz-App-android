package com.example.trivia_quizz_app.dataLayer.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QuestionsAndAnswers")
data class QuestionAAnswers (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val quizzName: String,
    val question: String,
    val correctAnswer: String,
    val wrongAnswer1: String,
    val wrongAnswer2: String,
    val wrongAnswer3: String
)