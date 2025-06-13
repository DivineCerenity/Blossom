package com.jonathon.blossom.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalTagDao {
    
    @Query("SELECT * FROM journal_tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<JournalTag>>
    
    @Query("SELECT * FROM journal_tags WHERE id = :id")
    suspend fun getTagById(id: Int): JournalTag?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: JournalTag): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<JournalTag>)
    
    @Update
    suspend fun updateTag(tag: JournalTag)
    
    @Delete
    suspend fun deleteTag(tag: JournalTag)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntryTagCrossRef(crossRef: JournalEntryTagCrossRef)
    
    @Delete
    suspend fun deleteEntryTagCrossRef(crossRef: JournalEntryTagCrossRef)
    
    @Query("DELETE FROM journal_entry_tag_cross_ref WHERE entryId = :entryId")
    suspend fun deleteAllTagsForEntry(entryId: Int)
    
    @Transaction
    @Query("SELECT * FROM journal_entries WHERE id = :entryId")
    suspend fun getEntryWithTags(entryId: Int): JournalEntryWithTags?
    
    @Transaction
    @Query("SELECT * FROM journal_entries ORDER BY creationTimestamp DESC")
    fun getAllEntriesWithTags(): Flow<List<JournalEntryWithTags>>
    
    @Transaction
    @Query("""
        SELECT * FROM journal_entries 
        WHERE id IN (
            SELECT entryId FROM journal_entry_tag_cross_ref 
            WHERE tagId = :tagId
        ) 
        ORDER BY creationTimestamp DESC
    """)
    fun getEntriesByTag(tagId: Int): Flow<List<JournalEntryWithTags>>
}
