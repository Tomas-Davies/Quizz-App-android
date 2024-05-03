package com.example.trivia_quizz_app.dataLayer

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trivia_quizz_app.dataLayer.entities.Quizz
import com.example.trivia_quizz_app.dataLayer.entities.QuestionAAnswers
import com.example.trivia_quizz_app.dataLayer.entities.relations.UserQuizzWithQuestions


@Dao
interface QuizzDao {

    @Insert
    fun insertQuizz(quizz: Quizz)

    @Insert
    fun insertQuestionAndAnswers(question: QuestionAAnswers)

    @Insert
    fun insertQuizzes(quizes: List<Quizz>)

    @Delete
    fun deleteQuizz(quizz: Quizz)

    @Delete
    fun deleteQuestion(question: QuestionAAnswers)

    @Delete
    fun deleteQuestionsAndAnswers(questions: List<QuestionAAnswers>)

    @Delete
    fun deleteQuizzes(quizes: List<Quizz>)

    @Update
    fun update(quizz: Quizz): Int

    @Query("SELECT * FROM Quizzes")
    fun getQuizzes(): List<UserQuizzWithQuestions>

    @Query("SELECT * FROM Quizzes WHERE quizzName LIKE :quizzName")
    fun findQuizzByName(quizzName: String): UserQuizzWithQuestions?

    @Query("SELECT * FROM QuestionsAndAnswers WHERE quizzName LIKE :quizzName")
    fun findQuestionsByQuizz(quizzName: String): List<QuestionAAnswers>
}