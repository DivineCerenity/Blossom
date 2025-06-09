package com.example.blossom.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class JournalEntryWithTags(
    @Embedded val entry: JournalEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            JournalEntryTagCrossRef::class,
            parentColumn = "entryId",
            entityColumn = "tagId"
        )
    )
    val tags: List<JournalTag>
)
