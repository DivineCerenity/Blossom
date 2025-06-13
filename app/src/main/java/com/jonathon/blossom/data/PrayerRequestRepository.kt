package com.jonathon.blossom.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRequestRepository @Inject constructor(
    private val prayerRequestDao: PrayerRequestDao
) {
    
    fun getAllPrayerRequests(): Flow<List<PrayerRequest>> = 
        prayerRequestDao.getAllPrayerRequests()
    
    fun getActivePrayerRequests(): Flow<List<PrayerRequest>> = 
        prayerRequestDao.getActivePrayerRequests()
    
    fun getAnsweredPrayerRequests(): Flow<List<PrayerRequest>> = 
        prayerRequestDao.getAnsweredPrayerRequests()
    
    fun getPrayerRequestsByCategory(category: PrayerCategory): Flow<List<PrayerRequest>> = 
        prayerRequestDao.getPrayerRequestsByCategory(category)
    
    suspend fun getPrayerRequestById(id: Int): PrayerRequest? = 
        prayerRequestDao.getPrayerRequestById(id)
    
    suspend fun insertPrayerRequest(prayerRequest: PrayerRequest) = 
        prayerRequestDao.insert(prayerRequest)
    
    suspend fun updatePrayerRequest(prayerRequest: PrayerRequest) = 
        prayerRequestDao.update(prayerRequest)
    
    suspend fun deletePrayerRequest(prayerRequest: PrayerRequest) = 
        prayerRequestDao.delete(prayerRequest)
    
    suspend fun markAsAnswered(id: Int) = 
        prayerRequestDao.markAsAnswered(id)
    
    suspend fun markAsUnanswered(id: Int) = 
        prayerRequestDao.markAsUnanswered(id)
    
    fun getActivePrayerCount(): Flow<Int> = 
        prayerRequestDao.getActivePrayerCount()
    
    fun getAnsweredPrayerCount(): Flow<Int> =
        prayerRequestDao.getAnsweredPrayerCountFlow()
}
