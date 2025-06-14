package com.jonathon.blossom.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jonathon.blossom.MainActivity
import com.jonathon.blossom.R
import com.jonathon.blossom.data.DailyHabit
import com.jonathon.blossom.data.DailyHabitRepository
import com.jonathon.blossom.data.BlossomDatabase
import com.jonathon.blossom.data.AnalyticsRepository

class HabitReminderWorker(
    private val context: Context,
    params: WorkerParameters,
    private val analyticsRepository: AnalyticsRepository
) : CoroutineWorker(context, params) {

    companion object {
        private const val CHANNEL_ID = "habit_reminders"
        private const val CHANNEL_NAME = "Habit Reminders"
        private const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        val habitId = inputData.getInt("habit_id", -1)
        val isTest = inputData.getBoolean("is_test", false)
        if (habitId == -1) return Result.failure()

        // Get database instance directly
        val database = Room.databaseBuilder(
            context,
            BlossomDatabase::class.java,
            "blossom_database"
        )
        .fallbackToDestructiveMigration()
        .addMigrations(
            BlossomDatabase.MIGRATION_1_2, 
            BlossomDatabase.MIGRATION_2_3, 
            BlossomDatabase.MIGRATION_3_4, 
            BlossomDatabase.MIGRATION_4_5, 
            BlossomDatabase.MIGRATION_5_6, 
            BlossomDatabase.MIGRATION_6_7, 
            BlossomDatabase.MIGRATION_7_8, 
            BlossomDatabase.MIGRATION_8_9, 
            BlossomDatabase.MIGRATION_9_10,
            BlossomDatabase.MIGRATION_10_11
        )
        .build()
        
        val habitDao = database.dailyHabitDao()
        val habit = habitDao.getHabitById(habitId) ?: return Result.failure()
        
        if (!habit.isEnabled) return Result.success()

        createNotificationChannel()
        showNotification(habit)

        // Only schedule the next reminder if this is not a test notification
        if (!isTest) {
            // Create repository manually since this is a worker context
            // We already have a database instance created earlier, reuse it
            val repository = DailyHabitRepository(habitDao, context, analyticsRepository)
            repository.scheduleReminder(habit)
        }

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