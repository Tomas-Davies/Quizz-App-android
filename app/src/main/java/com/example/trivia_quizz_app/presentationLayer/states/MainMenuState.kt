package com.example.trivia_quizz_app.presentationLayer.states

sealed class MainMenuState {
    data object Loading: MainMenuState()
    data object ShowingQuizzes: MainMenuState()
    data class Error(val messageId: Int): MainMenuState()
}