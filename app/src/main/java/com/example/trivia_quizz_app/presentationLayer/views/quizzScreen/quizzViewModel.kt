package com.example.trivia_quizz_app.presentationLayer.views.quizzScreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trivia_quizz_app.repositoryLayer.QuizzRepository


class QuizzViewModel(
    repository: QuizzRepository,
    val quizzName: String
): ViewModel() {

    private val quizzWithQuestions = repository.getQuizzByName(quizzName)
    private val quizzQuestions = quizzWithQuestions.questions
    private var currentRound = 0
    var currentQuestion = MutableLiveData(quizzQuestions[0].question)
    var currentCorrectAnswer = MutableLiveData(quizzQuestions[0].correctAnswer)
    var currentWrongAnswer1 = MutableLiveData(quizzQuestions[0].wrongAnswer1)
    var currentWrongAnswer2 = MutableLiveData(quizzQuestions[0].wrongAnswer2)
    var currentWrongAnswer3 = MutableLiveData(quizzQuestions[0].wrongAnswer3)

    var resultShowing = MutableLiveData(false)
    var streak = MutableLiveData(0)
    var answerBtnEnabled = MutableLiveData(true)
    var finished = MutableLiveData(false)


    private fun hasNextQuestion(): Boolean {
        return (currentRound) < quizzQuestions.size - 1
    }

    //TODO pokud bude posledni otazka tak zmeni tlacitko na FINISH a prejde na zobrazeni statistik
    fun nextRound(){
        if (hasNextQuestion()){
            currentRound++
            revertColor()
            resultShowing.value = false
            answerBtnEnabled.value = true
            updateData()
        }
    }

    private fun updateData(){
        currentQuestion.value = quizzQuestions[currentRound].question
        currentCorrectAnswer.value = quizzQuestions[currentRound].correctAnswer
        currentWrongAnswer1.value = quizzQuestions[currentRound].wrongAnswer1
        currentWrongAnswer2.value = quizzQuestions[currentRound].wrongAnswer2
        currentWrongAnswer3.value = quizzQuestions[currentRound].wrongAnswer3
    }

    fun checkUserAnswer(answer: String, id: Int) {
        var result = false
        if (answer == currentCorrectAnswer.value){
            result = true
        }
        showResult(result, id)
    }


    private fun showResult(correctChoice: Boolean, id: Int){
        val color = if (correctChoice) Color.Companion.Green else Color.Red
        // OBARVIT ODPOVEDI
        colorChosen(id)
        // vzdy pujde na dalsi
        if (hasNextQuestion()){
            resultShowing.value = true
        } else { finished.value = true }
        answerBtnEnabled.value = false
    }

    private fun colorChosen(id: Int){
        // nastavit barvu cislo id (pres when)
    }
    private fun revertColor(){

    }
}

class QuizzModelFactory(private val repository: QuizzRepository, private val quizzName: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizzViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return QuizzViewModel(repository, quizzName) as T
        }
        throw IllegalArgumentException("Unknown VieModel class")
    }
}