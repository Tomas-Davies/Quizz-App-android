package com.example.trivia_quizz_app.presentationLayer.views.mainMenuScreen


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trivia_quizz_app.dataLayer.entities.Quizz
import com.example.trivia_quizz_app.repositoryLayer.QuizzRepository
import kotlinx.coroutines.launch

class MainMenuViewModel(private val repository: QuizzRepository): ViewModel() {
    var quizzes by mutableStateOf(repository.getQuizzes().sortedBy { quizz -> !quizz.quizz.isFavourited })
        private set

    init {
        fillDefaultData()
    }

    fun update(quizz: Quizz): Int {
        val result = repository.update(quizz)
        loadQuizzes()
        return result
    }

    private fun fillDefaultData(){
        if (quizzes.isEmpty()) {
            repository.fillDefaultData()
            loadQuizzes()
        }
    }

    fun loadQuizzes(){
        quizzes = repository.getQuizzes().sortedBy { quizz -> !quizz.quizz.isFavourited }
    }

    fun deleteQuiz(quizz: Quizz){
        repository.deleteQuizz(quizz)
    }

}

class MainMenuModelFactory(private val repository: QuizzRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainMenuViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MainMenuViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown VieModel class")
    }
}