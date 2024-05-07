package com.example.trivia_quizz_app.repositoryLayer

import com.example.trivia_quizz_app.R
import com.example.trivia_quizz_app.dataLayer.MyConstants
import com.example.trivia_quizz_app.dataLayer.QuizzDao
import com.example.trivia_quizz_app.dataLayer.entities.QuestionAAnswers
import com.example.trivia_quizz_app.dataLayer.entities.Quizz
import com.example.trivia_quizz_app.dataLayer.entities.relations.UserQuizzWithQuestions


class QuizzRepository(private val quizzDao: QuizzDao) {
    suspend fun getQuizzes(): List<UserQuizzWithQuestions>{
        return quizzDao.getQuizzes()
    }

    fun getQuizzByName(name: String): UserQuizzWithQuestions? {
        return quizzDao.findQuizzByName(name)
    }

    suspend fun update(quizz: Quizz): Int {
        return quizzDao.update(quizz)
    }

    suspend fun fillDefaultData() {
        val defaultQuizzes = listOf(
            Quizz(quizzName = "sport", image = R.drawable.sport_icon, category = MyConstants.SPORTS),
            Quizz(quizzName = "animals", image = R.drawable.animals_icon, category = MyConstants.ANIMALS),
            Quizz(quizzName = "movies", image = R.drawable.movies_icon, category = MyConstants.MOVIES),
            Quizz(quizzName = "geography", image = R.drawable.geography_icon, category = MyConstants.GEOGRAPHY),
            Quizz(quizzName = "games", image = R.drawable.game_icon, category = MyConstants.VIDEO_GAMES),
            Quizz(quizzName = "music", image = R.drawable.music_icon, category = MyConstants.MUSIC),
            Quizz(quizzName = "computers", image = R.drawable.computer_icon, category = MyConstants.COMPUTERS),
            Quizz(quizzName = "MIX", image = R.drawable.mix_icon, category = MyConstants.MIX)
        )
        quizzDao.insertQuizzes(defaultQuizzes)
    }

    suspend fun insertQuizz(quizz: Quizz, questions: List<QuestionAAnswers>){
        quizzDao.insertQuizz(quizz)
        for (question in questions){
            quizzDao.insertQuestionAndAnswers(question)
        }
    }

    suspend fun deleteQuizz(quizz: Quizz){
        val questions = quizzDao.findQuestionsByQuizz(quizz.quizzName)
        quizzDao.deleteQuestionsAndAnswers(questions)
        quizzDao.deleteQuizz(quizz)
    }
}