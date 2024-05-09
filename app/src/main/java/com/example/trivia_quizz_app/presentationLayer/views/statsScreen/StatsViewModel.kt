package com.example.trivia_quizz_app.presentationLayer.views.statsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trivia_quizz_app.dataLayer.SharedPreferencesManager
import com.example.trivia_quizz_app.repositoryLayer.ApiGraphRepository
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale

class StatsViewModel(isInDarkTheme: Boolean, repository: ApiGraphRepository): ViewModel() {
    var completed = SharedPreferencesManager.getInt("TOTAL_COMPLETED", 0)
    var answeredCorrectly = SharedPreferencesManager.getInt("TOTAL_ANSWERED_CORRECTLY", 0)
    var answeredWrong = SharedPreferencesManager.getInt("TOTAL_ANSWERED_WRONG", 0)
    var successRate = SharedPreferencesManager.getInt("TOTAL_SUCCESS_RATE", 0)
    var maxStreak = SharedPreferencesManager.getInt("MAX_STREAK", 0)
    val pieGraph = MutableStateFlow<String?>(null)

    init {
        var graphUrl = repository.getPieGraphUrl(answeredCorrectly, answeredWrong)
        if (Locale.getDefault().language == "cs") {
            graphUrl = graphUrl
                .replace("correct answers", "správně")
                .replace("wrong answers", "špatně")
        }
        if (isInDarkTheme) {
            pieGraph.value = graphUrl
        } else {
            val updatedGraphUrl = graphUrl.replace("white", "black")
            pieGraph.value = updatedGraphUrl
        }
    }
}

class StatsViewModelFactory(
    private val isInDarkTheme: Boolean,
    private val repository: ApiGraphRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(isInDarkTheme, repository) as T
        }
        throw IllegalArgumentException("Unknown VieModel class")
    }
}