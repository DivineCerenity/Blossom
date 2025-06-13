package com.example.blossom.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerRequestDao {
    
    @Query("SELECT * FROM prayer_requests ORDER BY priority DESC, createdDate DESC")
    fun getAllPrayerRequests(): Flow<List<PrayerRequest>>
    
    @Query("SELECT * FROM prayer_requests WHERE isAnswered = 0 ORDER BY priority DESC, createdDate DESC")
    fun getActivePrayerRequests(): Flow<List<PrayerRequest>>
    
    @Query("SELECT * FROM prayer_requests WHERE isAnswered = 1 ORDER BY answeredDate DESC")
    fun getAnsweredPrayerRequests(): Flow<List<PrayerRequest>>
    
    @Query("SELECT * FROM prayer_requests WHERE category = :category ORDER BY priority DESC, createdDate DESC")
    fun getPrayerRequestsByCategory(category: PrayerCategory): Flow<List<PrayerRequest>>
    
    @Query("SELECT * FROM prayer_requests WHERE id = :id")
    suspend fun getPrayerRequestById(id: Int): PrayerRequest?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prayerRequest: PrayerRequest)
    
    @Update
    suspend fun update(prayerRequest: PrayerRequest)
    
    @Delete
    suspend fun delete(prayerRequest: PrayerRequest)
    
    @Query("UPDATE prayer_requests SET isAnswered = 1, answeredDate = :answeredDate WHERE id = :id")
    suspend fun markAsAnswered(id: Int, answeredDate: Long = System.currentTimeMillis())
    
    @Query("UPDATE prayer_requests SET isAnswered = 0, answeredDate = NULL WHERE id = :id")
    suspend fun markAsUnanswered(id: Int)
    
    @Query("SELECT COUNT(*) FROM prayer_requests WHERE isAnswered = 0")
    fun getActivePrayerCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM prayer_requests WHERE isAnswered = 1")
    fun getAnsweredPrayerCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM prayer_requests")
    suspend fun getTotalPrayerCount(): Int

    @Query("SELECT COUNT(*) FROM prayer_requests WHERE isAnswered = 1")
    suspend fun getAnsweredPrayerCount(): Int
}
