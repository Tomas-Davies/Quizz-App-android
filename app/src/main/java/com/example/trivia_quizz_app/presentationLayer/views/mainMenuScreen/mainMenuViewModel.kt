package com.example.trivia_quizz_app.presentationLayer.views.mainMenuScreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trivia_quizz_app.R
import com.example.trivia_quizz_app.dataLayer.entities.Quizz
import com.example.trivia_quizz_app.dataLayer.entities.relations.UserQuizzWithQuestions
import com.example.trivia_quizz_app.presentationLayer.states.MainMenuState
import com.example.trivia_quizz_app.repositoryLayer.QuizzRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainMenuViewModel(private val repository: QuizzRepository): ViewModel() {
    private val _quizzes: MutableStateFlow<List<UserQuizzWithQuestions>> = MutableStateFlow(emptyList())
    val quizzes = _quizzes.asStateFlow()

    private val _menuState = MutableStateFlow<MainMenuState>(MainMenuState.Loading)
    val menuState: StateFlow<MainMenuState> = _menuState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchQuizzes()
            defaultQuizzesInit()
        }
    }


    private suspend fun defaultQuizzesInit(){
        try {
            if (quizzes.value.isEmpty()) {
                repository.fillDefaultData()
                fetchLocalData()
            }
        } catch (e: Exception){
            _menuState.value = MainMenuState.Error(R.string.quiz_loading_error)
        }
    }

    fun fetchQuizzes(){
        viewModelScope.launch {
            fetchLocalData()
            _menuState.value = MainMenuState.ShowingQuizzes
        }
    }

    private suspend fun fetchLocalData(){
        _quizzes.value = repository.getQuizzes().sortedBy { quizz -> !quizz.quizz.isFavourited }
    }


    fun deleteQuizz(quizz: Quizz){
        viewModelScope.launch {
            repository.deleteQuizz(quizz)
            fetchQuizzes()
        }
    }

    fun updateQuizz(quizz: Quizz) {
        viewModelScope.launch {
            repository.update(quizz)
            fetchQuizzes()
        }
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