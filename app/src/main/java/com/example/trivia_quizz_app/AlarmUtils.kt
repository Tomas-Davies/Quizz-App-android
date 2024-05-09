package com.example.trivia_quizz_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmUtils(private val context: Context) {
    private var alarmManager: AlarmManager? = null
    private var alarmIntent: PendingIntent

    init {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReciever::class.java).let { mIntent ->
            PendingIntent.getBroadcast(context, 666, mIntent, PendingIntent.FLAG_IMMUTABLE)
        }
    }

    fun initRepeatingAlarm(calendar: Calendar){
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
    }
}