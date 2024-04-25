package com.example.trivia_quizz_app.presentationLayer.views.quizzScreen

import androidx.compose.runtime.livedata.observeAsState
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
    var quizzQuestions = quizzWithQuestions.questions
    private set

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
    var correctButtonId = -1

    var btn1Color = MutableLiveData<Color>()
    var btn2Color = MutableLiveData<Color>()
    var btn3Color = MutableLiveData<Color>()
    var btn4Color = MutableLiveData<Color>()

    private fun hasNextQuestion(): Boolean {
        return (currentRound) < quizzQuestions.size - 1
    }

    //TODO pokud bude posledni otazka tak zmeni tlacitko na FINISH a prejde na zobrazeni statistik
    fun nextRound(){
        if (hasNextQuestion()){
            currentRound++
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

    fun onAnswerClicked(id: Int) {
        showResult(id)
    }

    private fun showResult(id: Int){
        colorChosen(id)
        if (hasNextQuestion()){ resultShowing.value = true }
        else { finished.value = true }
        answerBtnEnabled.value = false
    }

    private fun colorChosen(id: Int){
       val buttons = listOf(btn1Color, btn2Color, btn3Color, btn4Color)
        buttons[correctButtonId].value = Color.Green
        if (id != correctButtonId){ buttons[id].value = Color.Red }
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