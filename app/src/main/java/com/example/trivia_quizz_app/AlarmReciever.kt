package com.example.trivia_quizz_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.trivia_quizz_app.presentationLayer.services.ReminderNotificationService
import java.util.Calendar

class AlarmReciever: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val reminderNotification = ReminderNotificationService(context)
        reminderNotification.showNotification()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val alarmUtils = AlarmUtils(context)
        alarmUtils.initRepeatingAlarm(calendar)
    }
}