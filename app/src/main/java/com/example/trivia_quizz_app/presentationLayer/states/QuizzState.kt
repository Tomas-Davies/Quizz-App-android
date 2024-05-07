package com.example.trivia_quizz_app.presentationLayer.states

sealed class QuizzState {
    data object NoInternetConnection: QuizzState()
    data object DownloadingLangModel: QuizzState()
    data object Loading: QuizzState()
    data class Question(
        val questionText: String,
        val answers: List<String>,
        val correctAnswerIndex: Int
    ) : QuizzState()
    data class Error(val messageId: Int): QuizzState()
    data object Finished: QuizzState()
}