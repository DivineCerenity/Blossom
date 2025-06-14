package com.jonathon.blossom

import android.app.Application
import androidx.work.Configuration
import com.jonathon.blossom.notifications.HabitReminderWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BlossomApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HabitReminderWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}