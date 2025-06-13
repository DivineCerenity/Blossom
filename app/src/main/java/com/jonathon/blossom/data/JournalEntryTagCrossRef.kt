package com.jonathon.blossom.data

import androidx.room.Entity

import androidx.room.Index

@Entity(
    tableName = "journal_entry_tag_cross_ref",
    primaryKeys = ["entryId", "tagId"],
    indices = [Index(value = ["tagId"])]
)
data class JournalEntryTagCrossRef(
    val entryId: Int,
    val tagId: Int
)
