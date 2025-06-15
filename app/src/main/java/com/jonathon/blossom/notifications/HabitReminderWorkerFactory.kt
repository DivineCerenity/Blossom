package com.jonathon.blossom.notifications

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.jonathon.blossom.data.DailyHabitRepository
import javax.inject.Inject

class HabitReminderWorkerFactory @Inject constructor(
    private val habitRepository: DailyHabitRepository
) : WorkerFactory() {    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            HabitReminderWorker::class.java.name ->
                HabitReminderWorker(appContext, workerParameters)
            DailyHabitResetWorker::class.java.name ->
                DailyHabitResetWorker(appContext, workerParameters)
            else -> null
        }
    }
}
