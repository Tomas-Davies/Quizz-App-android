package com.example.trivia_quizz_app.presentationLayer.views.quizzScreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trivia_quizz_app.repositoryLayer.ApiQuizzRepository
import com.example.trivia_quizz_app.repositoryLayer.QuizzRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizzData(
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)

sealed class QuizzState {
    data object Loading : QuizzState()
    data class Question(
        val questionText: String,
        val answers: List<String>,
        val correctAnswerIndex: Int
    ) : QuizzState()
    data class Error(val message: String) : QuizzState()
    data object Finished : QuizzState()
}

class QuizzViewModel(
    private val localRepository: QuizzRepository,
    private val apiRepository: ApiQuizzRepository,
    val quizzName: String,
    private val isUserCreated: Boolean,
    val category: Int
) : ViewModel() {
    private val _quizzState = MutableStateFlow<QuizzState>(QuizzState.Loading)
    val quizzState: StateFlow<QuizzState> = _quizzState.asStateFlow()

    private var currentRound = 0
    lateinit var quizzData: List<QuizzData>
        private set

    private val _showNextButton = MutableStateFlow(false)
    val showNextButton: StateFlow<Boolean> = _showNextButton.asStateFlow()

    private val _showFinishButton = MutableStateFlow(false)
    val showFinishButton: StateFlow<Boolean> = _showFinishButton.asStateFlow()

    private val _inputEnabled = MutableStateFlow(true)
    val inputEnabled: StateFlow<Boolean> = _inputEnabled.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    val btnColors: List<MutableStateFlow<Color>> = listOf(
        MutableStateFlow(Color.Unspecified),MutableStateFlow(Color.Unspecified),
        MutableStateFlow(Color.Unspecified),MutableStateFlow(Color.Unspecified)
    )

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                val data = if (isUserCreated) fetchLocalData() else fetchApiData()
                quizzData = data
                startQuizz()
            } catch (e: Exception) {
                _quizzState.value = QuizzState.Error("Error loading quiz data: ${e.message}")
            }
        }
    }

    private suspend fun fetchLocalData(): List<QuizzData> {
        val quizzWithQuestions = localRepository.getQuizzByName(quizzName)
        return quizzWithQuestions.questions.map { questionAndAnswers ->
            QuizzData(
                question = questionAndAnswers.question,
                correctAnswer = questionAndAnswers.correctAnswer,
                incorrectAnswers = listOf(questionAndAnswers.wrongAnswer1, questionAndAnswers.wrongAnswer2, questionAndAnswers.wrongAnswer3)
            )
        }
    }

    private suspend fun fetchApiData(): List<QuizzData> {
        val apiQuizz = apiRepository.getQuizz(category)
        return apiQuizz.apiQuestionAndAnswers.map { question ->
            QuizzData(
                question = question.question,
                correctAnswer = question.correct_answer,
                incorrectAnswers = question.incorrect_answers
            )
        }
    }


    fun startQuizz() {
        if (quizzData.isNotEmpty()) {
            val question = quizzData[currentRound]
            val answers = (question.incorrectAnswers + question.correctAnswer).shuffled()
            val correctIndex = answers.indexOf(question.correctAnswer)

            _quizzState.value = QuizzState.Question(
                questionText = question.question,
                answers = answers,
                correctAnswerIndex = correctIndex
            )
        } else {
            _quizzState.value = QuizzState.Finished
        }
    }

    fun onAnswerSelected(index: Int) {
        val currentState = _quizzState.value
        if (currentState is QuizzState.Question) {
            _inputEnabled.value = false
            val correctAnswerIndex = currentState.correctAnswerIndex
            btnColors[index].value = Color.Red
            btnColors[correctAnswerIndex].value = Color.Green
            if (index == correctAnswerIndex) {
                // zmena statistik
            } else {
                // zmena statistik
            }
            nextRound()
        }
    }

    private fun nextRound(){
        val currentState = _quizzState.value
        if (currentState is QuizzState.Question) {
            currentRound++
            if (currentRound < quizzData.size) {
                _showNextButton.value = true
            } else {
                _showFinishButton.value = true
            }
        }
    }

    fun onNextClicked(){
        val currentState = _quizzState.value
        if (currentState is QuizzState.Question) {
            _inputEnabled.value = true
            startQuizz()
            _showNextButton.value = false
        }
    }

    fun onFinishClicked(){
        val currentState = _quizzState.value
        if (currentState is QuizzState.Question) {
            // TODO SHOW STATISTICS
        }
    }

    fun setDefaultColors(defaultBtnColor: Color){
        btnColors[0].value = defaultBtnColor
        btnColors[1].value = defaultBtnColor
        btnColors[2].value = defaultBtnColor
        btnColors[3].value = defaultBtnColor
    }
}




class QuizzModelFactory(
    private val repository: QuizzRepository,
    private val apiRepository: ApiQuizzRepository,
    private val quizzName: String,
    private val isUserCreated: Boolean,
    private val category: Int = -1)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizzViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return QuizzViewModel(repository, apiRepository, quizzName, isUserCreated, category) as T
        }
        throw IllegalArgumentException("Unknown VieModel class")
    }
}