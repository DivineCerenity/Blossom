package com.example.blossom.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * This is the main database class for the entire application.
 * It's annotated with @Database to tell Room what this is.
 *
 * Hilt will use this blueprint in the AppModule to create the database instance.
 */
@Database(
    entities = [JournalEntry::class, DailyHabit::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BlossomDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun dailyHabitDao(): DailyHabitDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE daily_habits 
                    ADD COLUMN reminderTime INTEGER NOT NULL DEFAULT 0
                """)

                database.execSQL("""
                    ALTER TABLE daily_habits 
                    ADD COLUMN streakCount INTEGER NOT NULL DEFAULT 0
                """)

                database.execSQL("""
                    ALTER TABLE daily_habits 
                    ADD COLUMN isEnabled INTEGER NOT NULL DEFAULT 1
                """)
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the daily_habits table if it doesn't exist
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_habits (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL DEFAULT '',
                        reminderTime INTEGER NOT NULL DEFAULT 0,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        isEnabled INTEGER NOT NULL DEFAULT 1,
                        streakCount INTEGER NOT NULL DEFAULT 0,
                        lastCompletedDate INTEGER NOT NULL DEFAULT 0
                    )
                """)
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE journal_entries ADD COLUMN imageUri TEXT
                """)
            }
        }
    }
}