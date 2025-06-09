package com.example.blossom.ui.journal

data class AddEditJournalUiState(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val mood: String = "",
    val imageUrl: String? = null,
    val shouldNavigateBack: Boolean = false
)
