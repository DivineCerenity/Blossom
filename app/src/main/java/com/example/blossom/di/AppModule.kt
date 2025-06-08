package com.example.blossom.di

import android.content.Context
import androidx.room.Room
import com.example.blossom.data.BlossomDatabase
import com.example.blossom.data.JournalDao
import com.example.blossom.data.JournalRepository
import com.example.blossom.data.DailyHabitDao
import com.example.blossom.data.DailyHabitRepository
import com.example.blossom.network.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Database Providers ---

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): BlossomDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            BlossomDatabase::class.java,
            "blossom_database"
        )
            .addMigrations(BlossomDatabase.MIGRATION_1_2, BlossomDatabase.MIGRATION_2_3, BlossomDatabase.MIGRATION_3_4)
            .build()
    }

    @Provides
    fun provideJournalDao(db: BlossomDatabase): JournalDao {
        return db.journalDao()
    }

    @Provides
    fun provideJournalRepository(journalDao: JournalDao): JournalRepository {
        return JournalRepository(journalDao)
    }

    @Provides
    fun provideDailyHabitDao(db: BlossomDatabase): DailyHabitDao {
        return db.dailyHabitDao()
    }

    @Provides
    fun provideDailyHabitRepository(
        dailyHabitDao: DailyHabitDao,
        @ApplicationContext context: Context
    ): DailyHabitRepository {
        return DailyHabitRepository(dailyHabitDao, context)
    }

    // --- Context Provider ---
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    // --- Network Providers ---
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://beta.ourmanna.com/api/v1/") // Updated to the correct API endpoint
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}