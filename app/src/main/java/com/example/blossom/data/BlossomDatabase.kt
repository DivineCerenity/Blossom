package com.example.blossom.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        JournalEntry::class,
        DailyHabit::class,
        PrayerRequest::class,
        JournalTag::class,
        JournalEntryTagCrossRef::class
    ],
    version = 7, // Increment version number
    exportSchema = false
)
abstract class BlossomDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun dailyHabitDao(): DailyHabitDao
    abstract fun prayerRequestDao(): PrayerRequestDao
    abstract fun journalTagDao(): JournalTagDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Remove the is_favorited column if it exists, or simply don't add it
                // if it never existed.  This assumes you're OK with losing that data.
                // If you need to preserve it, you'll need a more complex migration.
                database.execSQL("ALTER TABLE journal_entries DROP COLUMN IF EXISTS is_favorited")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create prayer_requests table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS prayer_requests (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL DEFAULT '',
                        category TEXT NOT NULL DEFAULT 'PERSONAL',
                        priority TEXT NOT NULL DEFAULT 'MEDIUM',
                        isAnswered INTEGER NOT NULL DEFAULT 0,
                        createdDate INTEGER NOT NULL,
                        answeredDate INTEGER,
                        reminderEnabled INTEGER NOT NULL DEFAULT 0,
                        reminderTime INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create journal_tags table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS journal_tags (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        color TEXT NOT NULL DEFAULT '#6200EE'
                    )
                """.trimIndent())

                // Create journal_entry_tag_cross_ref table without foreign keys to match Room expectations
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS journal_entry_tag_cross_ref (
                        entryId INTEGER NOT NULL,
                        tagId INTEGER NOT NULL,
                        PRIMARY KEY(entryId, tagId)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Drop and recreate the cross-ref table without foreign keys
                database.execSQL("DROP TABLE IF EXISTS journal_entry_tag_cross_ref")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS journal_entry_tag_cross_ref (
                        entryId INTEGER NOT NULL,
                        tagId INTEGER NOT NULL,
                        PRIMARY KEY(entryId, tagId)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add imageUrls column to journal_entries table
                database.execSQL("ALTER TABLE journal_entries ADD COLUMN imageUrls TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add featuredImageUrl column to journal_entries table
                database.execSQL("ALTER TABLE journal_entries ADD COLUMN featuredImageUrl TEXT")
            }
        }
    }
}