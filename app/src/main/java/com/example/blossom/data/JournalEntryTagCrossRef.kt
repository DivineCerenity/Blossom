package com.example.blossom.data

import androidx.room.Entity

@Entity(
    tableName = "journal_entry_tag_cross_ref",
    primaryKeys = ["entryId", "tagId"]
)
data class JournalEntryTagCrossRef(
    val entryId: Int,
    val tagId: Int
)
