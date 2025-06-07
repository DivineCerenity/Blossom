package com.example.blossom.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * This is the main database class for the entire application.
 * It's annotated with @Database to tell Room what this is.
 *
 * Hilt will use this blueprint in the AppModule to create the database instance.
 */
@Database(
    entities = [JournalEntry::class],
    version = 2, // Keep this at 2
    exportSchema = false
)
abstract class BlossomDatabase : RoomDatabase() {

    // This abstract function tells Room that this database provides a JournalDao.
    // Room will generate the necessary code for us. We don't need anything else.
    abstract fun journalDao(): JournalDao
}