package com.jonathon.blossom.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_tags")
data class JournalTag(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String = "#6200EE" // Default purple color
)

// Predefined tags for common journal themes
object PredefinedTags {
    val GRATITUDE = JournalTag(name = "Gratitude", color = "#4CAF50")
    val PRAYER = JournalTag(name = "Prayer", color = "#2196F3")
    val REFLECTION = JournalTag(name = "Reflection", color = "#FF9800")
    val GOALS = JournalTag(name = "Goals", color = "#9C27B0")
    val FAMILY = JournalTag(name = "Family", color = "#E91E63")
    val WORK = JournalTag(name = "Work", color = "#607D8B")
    val HEALTH = JournalTag(name = "Health", color = "#4CAF50")
    val RELATIONSHIPS = JournalTag(name = "Relationships", color = "#F44336")
    val SPIRITUAL = JournalTag(name = "Spiritual", color = "#673AB7")
    val PERSONAL_GROWTH = JournalTag(name = "Personal Growth", color = "#009688")
    
    fun getDefaultTags(): List<JournalTag> = listOf(
        GRATITUDE, PRAYER, REFLECTION, GOALS, FAMILY,
        WORK, HEALTH, RELATIONSHIPS, SPIRITUAL, PERSONAL_GROWTH
    )
}
