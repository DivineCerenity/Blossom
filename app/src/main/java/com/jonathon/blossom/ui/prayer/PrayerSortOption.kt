package com.jonathon.blossom.ui.prayer

enum class PrayerSortOption(val label: String) {
    PRIORITY_HIGH_FIRST("Priority: High to Low"),
    PRIORITY_LOW_FIRST("Priority: Low to High"),
    NEWEST_FIRST("Newest First"),
    OLDEST_FIRST("Oldest First"),
    CATEGORY_A_TO_Z("Category: A to Z"),
    CATEGORY_Z_TO_A("Category: Z to A"),
    ANSWERED_FIRST("Answered First"),
    UNANSWERED_FIRST("Unanswered First")
}
