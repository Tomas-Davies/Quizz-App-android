package com.example.trivia_quizz_app.dataLayer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trivia_quizz_app.dataLayer.entities.QuestionAAnswers
import com.example.trivia_quizz_app.dataLayer.entities.Quizz

@Database(
    entities = [
        Quizz::class,
        QuestionAAnswers::class,
    ],
    version = 4
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun quizzDao(): QuizzDao

    companion object {
        private var instance: AppDatabase? = null

        fun getDatabase(ctx: Context): AppDatabase{
            if (instance != null){
                return instance as AppDatabase
            } else{
                instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    AppDatabase::class.java,
                    "AppDatabase"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as AppDatabase
        }
    }
}