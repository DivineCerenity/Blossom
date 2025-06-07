package com.example.blossom.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.blossom.data.JournalEntry
import com.example.blossom.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class SortOption(val label: String) {
    NEWEST("Newest First"),
    OLDEST("Oldest First"),
    HAPPY_FIRST("Happy First"),
    SAD_FIRST("Sad First")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalListScreen(
    viewModel: JournalListViewModel,
    onNavigateToAddEntry: () -> Unit,
    onNavigateToEditEntry: (Int) -> Unit
) {
    val entries by viewModel.entries.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState(initial = "")
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf(SortOption.NEWEST) }

    Scaffold(
        topBar = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search journal entries...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                imageVector = when (selectedSortOption) {
                                    SortOption.NEWEST -> Icons.Default.ArrowDownward
                                    SortOption.OLDEST -> Icons.Default.ArrowUpward
                                    SortOption.HAPPY_FIRST -> Icons.Default.SentimentVerySatisfied
                                    SortOption.SAD_FIRST -> Icons.Default.SentimentVeryDissatisfied
                                },
                                contentDescription = "Sort"
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        selectedSortOption = option
                                        showSortMenu = false
                                        viewModel.onSortOptionSelected(option)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when (option) {
                                                SortOption.NEWEST -> Icons.Default.ArrowDownward
                                                SortOption.OLDEST -> Icons.Default.ArrowUpward
                                                SortOption.HAPPY_FIRST -> Icons.Default.SentimentVerySatisfied
                                                SortOption.SAD_FIRST -> Icons.Default.SentimentVeryDissatisfied
                                            },
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                singleLine = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddEntry,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { padding ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "No entries match your search" else "No journal entries yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(entries) { entry ->
                    JournalEntryCard(
                        entry = entry,
                        onClick = { onNavigateToEditEntry(entry.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mood icon with background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = when (entry.mood) {
                            "Happy" -> HappyColor.copy(alpha = 0.2f)
                            "Neutral" -> NeutralColor.copy(alpha = 0.2f)
                            "Sad" -> SadColor.copy(alpha = 0.2f)
                            "Excited" -> ExcitedColor.copy(alpha = 0.2f)
                            "Grateful" -> GratefulColor.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                MoodIconDisplay(
                    mood = entry.mood,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(entry.creationTimestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MoodIconDisplay(mood: String, modifier: Modifier = Modifier) {
    val icon = when (mood) {
        "Happy" -> Icons.Default.SentimentVerySatisfied
        "Neutral" -> Icons.Default.SentimentNeutral
        "Sad" -> Icons.Default.SentimentVeryDissatisfied
        "Excited" -> Icons.Default.Celebration
        "Grateful" -> Icons.Default.Favorite
        else -> Icons.Default.SentimentNeutral
    }

    val tint = when (mood) {
        "Happy" -> HappyColor
        "Neutral" -> NeutralColor
        "Sad" -> SadColor
        "Excited" -> ExcitedColor
        "Grateful" -> GratefulColor
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Icon(
        imageVector = icon,
        contentDescription = mood,
        modifier = modifier,
        tint = tint
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy, h:mm a", Locale.getDefault())
    val date = Date(timestamp)
    return sdf.format(date)
}
