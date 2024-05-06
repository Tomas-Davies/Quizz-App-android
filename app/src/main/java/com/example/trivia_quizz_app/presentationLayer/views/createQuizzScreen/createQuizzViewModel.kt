package com.example.trivia_quizz_app.presentationLayer.views.createQuizzScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trivia_quizz_app.dataLayer.MyConstants
import com.example.trivia_quizz_app.dataLayer.entities.QuestionAAnswers
import com.example.trivia_quizz_app.dataLayer.entities.Quizz
import com.example.trivia_quizz_app.repositoryLayer.QuizzRepository
import kotlinx.coroutines.launch

class CreateQuizzViewModel(private val repository: QuizzRepository): ViewModel(){

    var quizzName: String = ""
    val questions: MutableList<QuestionAAnswers> = mutableListOf()


    fun addQuestion(question: QuestionAAnswers){
        questions.add(question)
    }

    fun addQuizzToRepository(){
        viewModelScope.launch {
            val quizz = Quizz(quizzName)
            repository.insertQuizz(quizz, questions)
        }
    }

    fun nameIsAvailable(name: String): Boolean {
        return (repository.getQuizzByName(name) == null)
    }

    fun checkQuestionsInput(input: List<String>): Boolean {
        for (w in input){
            if (w.isEmpty()) return false
        }
        return true
    }

    fun checkIfHasQuestions(): Int {
        return if (questions.size < MyConstants.QUIZZ_QUESTION_COUNT) 0 else 1
    }
}


class CreateQuizzModelFactory(private val repository: QuizzRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateQuizzViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return CreateQuizzViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown VieModel class")
    }
}