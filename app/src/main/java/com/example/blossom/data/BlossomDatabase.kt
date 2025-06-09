package com.example.blossom.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [JournalEntry::class, DailyHabit::class],
    version = 2, // Increment version number
    exportSchema = false
)
abstract class BlossomDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun dailyHabitDao(): DailyHabitDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Remove the is_favorited column if it exists, or simply don't add it
                // if it never existed.  This assumes you're OK with losing that data.
                // If you need to preserve it, you'll need a more complex migration.
                database.execSQL("ALTER TABLE journal_entries DROP COLUMN IF EXISTS is_favorited")
            }
        }
    }
}