package com.example.blossom.ui.journal

data class AddEditJournalUiState(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val mood: String = "",
    val imageUrl: String? = null, // Keep for backward compatibility
    val imageUrls: List<String> = emptyList(), // Multiple images
    val featuredImageUrl: String? = null, // Featured image for the card
    val shouldNavigateBack: Boolean = false
)
