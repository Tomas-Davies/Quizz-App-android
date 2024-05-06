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
    suspend fun insertQuizz(quizz: Quizz)

    @Insert
    suspend fun insertQuestionAndAnswers(question: QuestionAAnswers)

    @Insert
    suspend fun insertQuizzes(quizes: List<Quizz>)

    @Delete
    suspend fun deleteQuizz(quizz: Quizz)

    @Delete
    suspend fun deleteQuestion(question: QuestionAAnswers)

    @Delete
    suspend fun deleteQuestionsAndAnswers(questions: List<QuestionAAnswers>)

    @Delete
    suspend fun deleteQuizzes(quizes: List<Quizz>)

    @Update
    suspend fun update(quizz: Quizz): Int

    @Query("SELECT * FROM Quizzes")
    suspend fun getQuizzes(): List<UserQuizzWithQuestions>

    @Query("SELECT * FROM Quizzes WHERE quizzName LIKE :quizzName")
    fun findQuizzByName(quizzName: String): UserQuizzWithQuestions?

    @Query("SELECT * FROM QuestionsAndAnswers WHERE quizzName LIKE :quizzName")
    suspend fun findQuestionsByQuizz(quizzName: String): List<QuestionAAnswers>
}