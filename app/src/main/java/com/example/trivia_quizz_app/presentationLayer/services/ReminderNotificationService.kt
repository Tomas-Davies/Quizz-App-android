package com.example.trivia_quizz_app.presentationLayer.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.trivia_quizz_app.R
import com.example.trivia_quizz_app.presentationLayer.views.mainMenuScreen.MainMenuScreen

class ReminderNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun showNotification() {
        val activityIntent = Intent(context, MainMenuScreen::class.java)
        val activityPendingIntent = PendingIntent
                .getActivity(
                    context,
                    1,
                    activityIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_local_play_24)
            .setContentTitle("Time for a quizz?")
            .setContentText("Let a quick quizz brighten your day!")
            .setContentIntent(activityPendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }
    companion object {
        const val REMINDER_CHANNEL_ID = "reminder"
    }
}