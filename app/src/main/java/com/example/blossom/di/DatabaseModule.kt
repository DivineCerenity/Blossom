package com.example.blossom.di

import android.content.Context
import androidx.room.Room
import com.example.blossom.data.BlossomDatabase
import com.example.blossom.data.DailyHabitDao
import com.example.blossom.data.JournalDao
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
        .addMigrations(BlossomDatabase.MIGRATION_1_2) // Add migration
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
}