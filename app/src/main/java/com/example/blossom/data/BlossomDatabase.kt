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
        JournalEntryTagCrossRef::class,
        DailyVerse::class,
        MeditationSession::class,  // 📊 NEW ANALYTICS ENTITIES
        DailyAnalytics::class,
        Achievement::class
    ],
    version = 10,  // 📊 INCREMENTED VERSION FOR JOURNAL SCHEMA FIX
    exportSchema = false
)
abstract class BlossomDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun dailyHabitDao(): DailyHabitDao
    abstract fun prayerRequestDao(): PrayerRequestDao
    abstract fun journalTagDao(): JournalTagDao
    abstract fun dailyVerseDao(): DailyVerseDao
    abstract fun analyticsDao(): AnalyticsDao  // 📊 NEW ANALYTICS DAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE journal_entries DROP COLUMN IF EXISTS is_favorited")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
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
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS journal_tags (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        color TEXT NOT NULL DEFAULT '#6200EE'
                    )
                """.trimIndent())

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
                database.execSQL("ALTER TABLE journal_entries ADD COLUMN imageUrls TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE journal_entries ADD COLUMN featuredImageUrl TEXT")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `daily_verses` (
                        `date` TEXT NOT NULL,
                        `verse` TEXT NOT NULL,
                        `reference` TEXT NOT NULL,
                        `fetchedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`date`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 📊 CREATE ANALYTICS TABLES
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `meditation_sessions` (
                        `id` TEXT NOT NULL,
                        `startTime` INTEGER NOT NULL,
                        `endTime` INTEGER NOT NULL,
                        `duration` INTEGER NOT NULL,
                        `breathingPattern` TEXT NOT NULL,
                        `binauralBeat` TEXT,
                        `backgroundSound` TEXT,
                        `theme` TEXT NOT NULL,
                        `completed` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `daily_analytics` (
                        `date` TEXT NOT NULL,
                        `meditationTime` INTEGER NOT NULL,
                        `meditationSessions` INTEGER NOT NULL,
                        `journalEntries` INTEGER NOT NULL,
                        `prayersAdded` INTEGER NOT NULL,
                        `prayersAnswered` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`date`)
                    )
                """.trimIndent())

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `achievements` (
                        `id` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `icon` TEXT NOT NULL,
                        `unlockedAt` INTEGER,
                        `category` TEXT NOT NULL,
                        `threshold` INTEGER NOT NULL,
                        `currentProgress` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 📝 FIX JOURNAL ENTRIES TABLE SCHEMA
                // Handle column differences between expected and found schema

                // First, check if the problematic columns exist and handle them
                try {
                    // Remove columns that shouldn't be there
                    database.execSQL("""
                        CREATE TABLE IF NOT EXISTS `journal_entries_new` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `title` TEXT NOT NULL,
                            `content` TEXT NOT NULL,
                            `creationTimestamp` INTEGER NOT NULL,
                            `mood` TEXT NOT NULL,
                            `imageUrls` TEXT NOT NULL DEFAULT '',
                            `featuredImageUrl` TEXT,
                            `imageUrl` TEXT
                        )
                    """.trimIndent())

                    // Copy data from old table to new table
                    database.execSQL("""
                        INSERT INTO journal_entries_new (id, title, content, creationTimestamp, mood, imageUrls, featuredImageUrl, imageUrl)
                        SELECT
                            id,
                            title,
                            content,
                            creationTimestamp,
                            mood,
                            COALESCE(imageUrls, '') as imageUrls,
                            featuredImageUrl,
                            COALESCE(imageUri, imageUrl) as imageUrl
                        FROM journal_entries
                    """.trimIndent())

                    // Drop old table and rename new one
                    database.execSQL("DROP TABLE journal_entries")
                    database.execSQL("ALTER TABLE journal_entries_new RENAME TO journal_entries")

                } catch (e: Exception) {
                    // If migration fails, just recreate the table
                    database.execSQL("DROP TABLE IF EXISTS journal_entries")
                    database.execSQL("""
                        CREATE TABLE IF NOT EXISTS `journal_entries` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `title` TEXT NOT NULL,
                            `content` TEXT NOT NULL,
                            `creationTimestamp` INTEGER NOT NULL,
                            `mood` TEXT NOT NULL,
                            `imageUrls` TEXT NOT NULL DEFAULT '',
                            `featuredImageUrl` TEXT,
                            `imageUrl` TEXT
                        )
                    """.trimIndent())
                }
            }
        }
    }
}