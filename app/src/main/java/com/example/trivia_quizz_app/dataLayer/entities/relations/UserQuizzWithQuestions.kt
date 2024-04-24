package com.example.trivia_quizz_app.dataLayer.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.trivia_quizz_app.dataLayer.entities.QuestionAAnswers
import com.example.trivia_quizz_app.dataLayer.entities.Quizz

data class UserQuizzWithQuestions(
    @Embedded val quizz: Quizz,
    @Relation(
        parentColumn = "quizzName",
        entityColumn = "quizzName"
    )
    val questions: List<QuestionAAnswers>
)