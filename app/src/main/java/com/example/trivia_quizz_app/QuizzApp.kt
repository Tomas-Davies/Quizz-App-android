package com.example.trivia_quizz_app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.trivia_quizz_app.dataLayer.AppDatabase
import com.example.trivia_quizz_app.dataLayer.SharedPreferencesManager
import com.example.trivia_quizz_app.presentationLayer.services.ReminderNotificationService
import com.example.trivia_quizz_app.repositoryLayer.QuizzRepository


class QuizzApp: Application() {
    val db by lazy { AppDatabase.getDatabase(this) }
    val quizzRepo by lazy { QuizzRepository(db.quizzDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        SharedPreferencesManager.init(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel (
                ReminderNotificationService.REMINDER_CHANNEL_ID,
                "Reminder",
                NotificationManager.IMPORTANCE_HIGH
                )
            channel.description = "Used for timed reminder notifications"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}