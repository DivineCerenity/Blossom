package com.jonathon.blossom.di

import com.jonathon.blossom.data.DailyHabitRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerAppEntryPoint {
    fun dailyHabitRepository(): DailyHabitRepository
}
