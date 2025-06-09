package com.example.blossom.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_requests")
data class PrayerRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val category: PrayerCategory = PrayerCategory.PERSONAL,
    val priority: PrayerPriority = PrayerPriority.MEDIUM,
    val isAnswered: Boolean = false,
    val createdDate: Long = System.currentTimeMillis(),
    val answeredDate: Long? = null,
    val reminderEnabled: Boolean = false,
    val reminderTime: Long = 0 // Time in milliseconds for daily reminder
)

enum class PrayerCategory(val displayName: String) {
    PERSONAL("Personal"),
    FAMILY("Family"),
    FRIENDS("Friends"),
    HEALTH("Health"),
    WORK("Work/Career"),
    CHURCH("Church"),
    WORLD("World/Global"),
    GRATITUDE("Gratitude"),
    OTHER("Other")
}

enum class PrayerPriority(val displayName: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4)
}
