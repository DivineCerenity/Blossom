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
        DailyVerse::class
    ],
    version = 8,
    exportSchema = false
)
abstract class BlossomDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun dailyHabitDao(): DailyHabitDao
    abstract fun prayerRequestDao(): PrayerRequestDao
    abstract fun journalTagDao(): JournalTagDao
    abstract fun dailyVerseDao(): DailyVerseDao

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
    }
}