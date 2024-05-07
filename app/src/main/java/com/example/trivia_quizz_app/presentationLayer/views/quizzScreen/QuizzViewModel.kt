package com.example.trivia_quizz_app.presentationLayer.views.quizzScreen

import android.text.Html
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trivia_quizz_app.R
import com.example.trivia_quizz_app.dataLayer.SharedPreferencesManager
import com.example.trivia_quizz_app.presentationLayer.states.QuizzState
import com.example.trivia_quizz_app.repositoryLayer.ApiQuizzRepository
import com.example.trivia_quizz_app.repositoryLayer.QuizzRepository
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class QuizzData(
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)


class QuizzViewModel(
    var hasInternetConnection: Boolean,
    private val localRepository: QuizzRepository,
    private val apiRepository: ApiQuizzRepository,
    val quizzName: String,
    private val isUserCreated: Boolean,
    val category: Int
) : ViewModel() {
    private val _quizzState = MutableStateFlow<QuizzState>(QuizzState.Loading)
    val quizzState: StateFlow<QuizzState> = _quizzState.asStateFlow()

    private var _currentRound = 0

    lateinit var quizzData: List<QuizzData>

    private val _questionCount = MutableStateFlow(0)
    val questionCount = _questionCount.asStateFlow()

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
    val currentRound = MutableStateFlow(1)

    val btnColors: List<MutableStateFlow<Color>> = listOf(
        MutableStateFlow(Color.Unspecified),MutableStateFlow(Color.Unspecified),
        MutableStateFlow(Color.Unspecified),MutableStateFlow(Color.Unspecified)
    )

    private lateinit var englishToCzechTranslator: Translator

    init {
        prepareViewModel()
    }

    fun prepareViewModel() {
        val czechLangOnDevice = Locale.getDefault().language == "cs"
        if (czechLangOnDevice){
            if (hasInternetConnection) {
                prepareMlKit()
            }
            else {
                _quizzState.value = QuizzState.NoInternetConnection
            }
        }
        else {
            fetchData()
        }
    }

    private fun prepareMlKit() {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.CZECH)
            .build()
        englishToCzechTranslator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        _quizzState.value = QuizzState.DownloadingLangModel
        englishToCzechTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                _quizzState.value = QuizzState.Loading
                fetchData()
            }
            .addOnFailureListener {
                _quizzState.value = QuizzState.Error(R.string.quiz_loading_error)
            }
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                var data: List<QuizzData> = emptyList()
                if (isUserCreated){
                    data = fetchLocalData()
                }
                else if (hasInternetConnection){
                    data = fetchApiData()
                }
                else {
                    _quizzState.value = QuizzState.NoInternetConnection
                }
                quizzData = data
                _questionCount.value = data.size
                startQuizz()
            } catch (e: Exception) {
                _quizzState.value = QuizzState.Error(R.string.quiz_loading_error)
                Log.e("QuizzDataFetch", "${e.message}")
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
        val allData = apiQuizz.results.shuffled().filter { data ->
            data.correct_answer.length < 32 && data.incorrect_answers.find { text -> text.length > 32 } == null
        }
        val data = allData.subList(0,10)
        val processedData: List<QuizzData>

        if (Locale.getDefault().language == "cs") {
           processedData = data.map { question ->
                QuizzData(
                    question = question.question.parseHtmlString().translateToCzech(),
                    correctAnswer = question.correct_answer.parseHtmlString().translateToCzech(),
                    incorrectAnswers = question.incorrect_answers.map { answer ->
                        answer.parseHtmlString().translateToCzech()
                    }
                )
            }
            englishToCzechTranslator.close()
        } else {
            processedData = data.map { question ->
                QuizzData(
                    question = question.question.parseHtmlString(),
                    correctAnswer = question.correct_answer.parseHtmlString(),
                    incorrectAnswers = question.incorrect_answers.map { answer -> answer.parseHtmlString() }
                )
            }
        }
        return processedData
    }

    private fun String.parseHtmlString(): String {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    private suspend fun String.translateToCzech(): String {
        return suspendCancellableCoroutine { continuation ->
            englishToCzechTranslator.translate(this)
                .addOnSuccessListener { translatedText ->
                    continuation.resume(translatedText)
                }
        }
    }

    private fun startQuizz() {
        if (quizzData.isNotEmpty()) {
            val question = quizzData[_currentRound]
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
            showNextBtn()
        }
    }

    private fun showNextBtn(){
        val currentState = _quizzState.value
        if (currentState is QuizzState.Question) {
            _currentRound++
            if (_currentRound < quizzData.size) {
                _showNextButton.value = true
            } else {
                _showFinishButton.value = true
            }
        }
    }

    fun onNextClicked(){
        val currentState = _quizzState.value
        if (currentState is QuizzState.Question) {
            currentRound.value++
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

    fun updateAllTotalResultData(){
        viewModelScope.launch {
            val currentState = _quizzState.value
            if (currentState is QuizzState.Finished){
                totalResultDataInc("TOTAL_COMPLETED")
                totalResultDataInc("TOTAL_ANSWERED_CORRECTLY", correctCount)
                totalResultDataInc("TOTAL_ANSWERED_WRONG", wrongCount)
                updateTotalSuccessRate()
                updateTotalMaxStreak()
            }
        }
    }

    private fun totalResultDataInc(prefName: String, inc: Int = 1){
        val oldValue = SharedPreferencesManager.getInt(prefName, 0)
        SharedPreferencesManager.saveInt(prefName, oldValue + inc)
    }

    private fun updateTotalSuccessRate(){
        val answeredWrong = SharedPreferencesManager.getInt("TOTAL_ANSWERED_WRONG", 1)
        val answeredCorrect = SharedPreferencesManager.getInt("TOTAL_ANSWERED_CORRECTLY", 1)
        val allAnswers = answeredCorrect + answeredWrong
        val percentage = (answeredCorrect * 100) / allAnswers
        SharedPreferencesManager.saveInt("TOTAL_SUCCESS_RATE",  percentage)
    }

    private fun updateTotalMaxStreak(){
        val oldMaxStreak = SharedPreferencesManager.getInt("MAX_STREAK", 0)
        if (oldMaxStreak < maxStreak){
            SharedPreferencesManager.saveInt("MAX_STREAK", maxStreak)
        }
    }

    fun getScore(): Int {
        val correctCount = correctCount
        val questionCount = quizzData.size
        return (correctCount * 100) / questionCount
    }
}



class QuizzModelFactory(
    private val hasInternetConnection: Boolean,
    private val repository: QuizzRepository,
    private val apiRepository: ApiQuizzRepository,
    private val quizzName: String,
    private val isUserCreated: Boolean,
    private val category: Int = -1)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizzViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return QuizzViewModel(hasInternetConnection, repository, apiRepository, quizzName, isUserCreated, category) as T
        }
        throw IllegalArgumentException("Unknown VieModel class")
    }
}