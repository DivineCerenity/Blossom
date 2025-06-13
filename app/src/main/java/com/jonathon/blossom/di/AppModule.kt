package com.jonathon.blossom.di

import android.content.Context
import com.jonathon.blossom.audio.MeditationAudioManager
import com.jonathon.blossom.network.ApiService
import com.jonathon.blossom.ui.settings.SettingsRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.jonathon.blossom.network.DriveApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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

    @Provides
    @Singleton
    @DriveRetrofit
    fun provideDriveRetrofit(moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/drive/v3/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideDriveApiService(@DriveRetrofit retrofit: Retrofit): DriveApiService {
        return retrofit.create(DriveApiService::class.java)
    }

    // --- Settings Repository ---
    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }

    // --- Audio Manager ---
    @Provides
    @Singleton
    fun provideMeditationAudioManager(@ApplicationContext context: Context): MeditationAudioManager {
        return MeditationAudioManager(context)
    }

    // --- Backup Manager ---
    @Provides
    @Singleton
    fun provideBackupManager(@ApplicationContext context: Context, database: com.jonathon.blossom.data.BlossomDatabase, moshi: Moshi, driveApiService: com.jonathon.blossom.network.DriveApiService): com.jonathon.blossom.ui.settings.BackupManager {
        return com.jonathon.blossom.ui.settings.BackupManager(context, database, moshi, driveApiService)
    }
}
