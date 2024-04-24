package com.example.trivia_quizz_app.dataLayer.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Quizzes")
data class Quizz (
    @PrimaryKey(autoGenerate = false)
    val quizzName: String,
    var isFavourited: Boolean = false,
    var image: Int = 0
    )