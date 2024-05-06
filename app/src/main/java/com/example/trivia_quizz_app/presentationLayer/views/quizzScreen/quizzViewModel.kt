package com.example.trivia_quizz_app.presentationLayer.views.quizzScreen

import android.text.Html
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trivia_quizz_app.R
import com.example.trivia_quizz_app.presentationLayer.states.QuizzState
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
    private lateinit var quizzData: List<QuizzData>

    private val _showNextButton = MutableStateFlow(false)
    val showNextButton: StateFlow<Boolean> = _showNextButton.asStateFlow()

    private val _showFinishButton = MutableStateFlow(false)
    val showFinishButton: StateFlow<Boolean> = _showFinishButton.asStateFlow()

    private val _inputEnabled = MutableStateFlow(true)
    val inputEnabled: StateFlow<Boolean> = _inputEnabled.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    val medalLimit = MutableStateFlow(0)

    var correctCount = 0
    var wrongCount = 0
    var maxStreak = 0

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
                _quizzState.value = QuizzState.Error(R.string.quiz_loading_error)
            }
        }
    }

    private fun fetchLocalData(): List<QuizzData> {
        val quizzWithQuestions = localRepository.getQuizzByName(quizzName)
        return quizzWithQuestions?.questions?.map { questionAndAnswers ->
            QuizzData(
                question = questionAndAnswers.question,
                correctAnswer = questionAndAnswers.correctAnswer,
                incorrectAnswers = listOf(questionAndAnswers.wrongAnswer1, questionAndAnswers.wrongAnswer2, questionAndAnswers.wrongAnswer3)
            )
        } ?: emptyList()
    }

    private suspend fun fetchApiData(): List<QuizzData>
    {
        val apiQuizz = apiRepository.getQuizz(category)
        val allData = apiQuizz.results.shuffled()
        val data = allData.subList(0,10)
        return data.map { question ->
            QuizzData(
                question = question.question.parseHtmlString(),
                correctAnswer = question.correct_answer.parseHtmlString(),
                incorrectAnswers = question.incorrect_answers.map { answer -> answer.parseHtmlString() }
            )
        }
    }

    private fun String.parseHtmlString(): String{
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    private fun startQuizz() {
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
            _quizzState.value = QuizzState.Error(R.string.quiz_loading_error)
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
                correctCount++
                _streak.value++
                if (_streak.value > maxStreak) {
                    maxStreak = _streak.value
                }
            } else {
                wrongCount++
                _streak.value = 0
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
            _quizzState.value = QuizzState.Finished
        }
    }

    fun setDefaultColors(defaultBtnColor: Color){
        val currentState = _quizzState.value
        if (currentState is QuizzState.Question) {
            btnColors[0].value = defaultBtnColor
            btnColors[1].value = defaultBtnColor
            btnColors[2].value = defaultBtnColor
            btnColors[3].value = defaultBtnColor
        }
    }

    fun getScore(): Int {
        val correctCount = correctCount
        val questionCount = quizzData.size
        return (correctCount * 100) / questionCount
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