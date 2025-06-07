package com.example.blossom.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.blossom.MainActivity
import com.example.blossom.R
import com.example.blossom.data.DailyHabit
import com.example.blossom.data.DailyHabitRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HabitReminderWorker @Inject constructor(
    @ApplicationContext private val context: Context,
    params: WorkerParameters,
    private val repository: DailyHabitRepository
) : CoroutineWorker(context, params) {

    companion object {
        private const val CHANNEL_ID = "habit_reminders"
        private const val CHANNEL_NAME = "Habit Reminders"
        private const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        val habitId = inputData.getInt("habit_id", -1)
        if (habitId == -1) return Result.failure()

        val habit = repository.getHabitById(habitId) ?: return Result.failure()
        
        if (!habit.isEnabled) return Result.success()

        createNotificationChannel()
        showNotification(habit)

        // Schedule the next reminder
        repository.scheduleReminder(habit)

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for daily habits"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(habit: DailyHabit) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Habit Reminder")
            .setContentText("Time to ${habit.title}!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
} 