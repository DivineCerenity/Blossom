package com.example.blossom.di

import android.content.Context
import androidx.room.Room
import com.example.blossom.data.AnalyticsDao
import com.example.blossom.data.BlossomDatabase
import com.example.blossom.data.DailyHabitDao
import com.example.blossom.data.DailyVerseDao
import com.example.blossom.data.JournalDao
import com.example.blossom.data.JournalTagDao
import com.example.blossom.data.PrayerRequestDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BlossomDatabase {
        return Room.databaseBuilder(
            context,
            BlossomDatabase::class.java,
            "blossom_database"
        )
        .fallbackToDestructiveMigration() // Allow destructive migration for all version changes
        .addMigrations(BlossomDatabase.MIGRATION_1_2, BlossomDatabase.MIGRATION_2_3, BlossomDatabase.MIGRATION_3_4, BlossomDatabase.MIGRATION_4_5, BlossomDatabase.MIGRATION_5_6, BlossomDatabase.MIGRATION_6_7, BlossomDatabase.MIGRATION_7_8, BlossomDatabase.MIGRATION_8_9, BlossomDatabase.MIGRATION_9_10) // Add migrations including journal schema fix
        .build()
    }

    @Provides
    @Singleton
    fun provideJournalDao(database: BlossomDatabase): JournalDao {
        return database.journalDao()
    }

    @Provides
    @Singleton
    fun provideDailyHabitDao(database: BlossomDatabase): DailyHabitDao {
        return database.dailyHabitDao()
    }

    @Provides
    @Singleton
    fun providePrayerRequestDao(database: BlossomDatabase): PrayerRequestDao {
        return database.prayerRequestDao()
    }

    @Provides
    @Singleton
    fun provideJournalTagDao(database: BlossomDatabase): JournalTagDao {
        return database.journalTagDao()
    }

    @Provides
    @Singleton
    fun provideDailyVerseDao(database: BlossomDatabase): DailyVerseDao {
        return database.dailyVerseDao()
    }

    @Provides
    @Singleton
    fun provideAnalyticsDao(database: BlossomDatabase): AnalyticsDao {
        return database.analyticsDao()  // 📊 NEW ANALYTICS DAO
    }
}