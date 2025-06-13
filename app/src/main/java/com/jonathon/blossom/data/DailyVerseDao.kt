package com.jonathon.blossom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyVerseDao {
    
    @Query("SELECT * FROM daily_verses WHERE date = :date LIMIT 1")
    suspend fun getVerseForDate(date: String): DailyVerse?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerse(verse: DailyVerse)
    
    @Query("DELETE FROM daily_verses WHERE date < :cutoffDate")
    suspend fun deleteOldVerses(cutoffDate: String)
    
    @Query("SELECT COUNT(*) FROM daily_verses")
    suspend fun getVerseCount(): Int
}
