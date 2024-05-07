package com.example.trivia_quizz_app.presentationLayer.views.statsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trivia_quizz_app.dataLayer.SharedPreferencesManager

class StatsViewModel: ViewModel() {
    var completed = SharedPreferencesManager.getInt("TOTAL_COMPLETED", 0)
    var answeredCorrectly = SharedPreferencesManager.getInt("TOTAL_ANSWERED_CORRECTLY", 0)
    var answeredWrong = SharedPreferencesManager.getInt("TOTAL_ANSWERED_WRONG", 0)
    var successRate = SharedPreferencesManager.getInt("TOTAL_SUCCESS_RATE", 0)
    var maxStreak = SharedPreferencesManager.getInt("MAX_STREAK", 0)
}

class StatsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel() as T
        }
        throw IllegalArgumentException("Unknown VieModel class")
    }
}