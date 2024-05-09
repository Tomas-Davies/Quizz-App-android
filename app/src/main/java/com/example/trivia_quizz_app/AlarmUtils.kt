package com.example.trivia_quizz_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmUtils(private val context: Context) {
    private var alarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun initRepeatingAlarm(calendar: Calendar){
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val alarmIntent = Intent(context, AlarmReciever::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 666, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
    }
}