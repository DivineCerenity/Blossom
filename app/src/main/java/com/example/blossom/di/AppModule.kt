package com.example.blossom.di

import android.content.Context
import androidx.room.Room
import com.example.blossom.data.BlossomDatabase
import com.example.blossom.data.JournalDao
import com.example.blossom.data.JournalRepository
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
            .fallbackToDestructiveMigration()
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
    fun provideApiService(moshi: Moshi): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://beta.ourmanna.com/api/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }
}