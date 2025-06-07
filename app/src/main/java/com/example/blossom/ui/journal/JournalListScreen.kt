package com.example.blossom.ui.journal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.blossom.data.JournalEntry
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JournalListScreen(
    viewModel: JournalListViewModel, // Takes ViewModel as a parameter
    onNavigateToAddEntry: () -> Unit,
    onNavigateToEditEntry: (Int) -> Unit
) {
    val entries by viewModel.entries.collectAsState(initial = emptyList())
    val entryToDelete by viewModel.entryToDelete.collectAsState()

    val showDeleteDialog = entryToDelete != null

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddEntry) {
                Icon(Icons.Default.Add, contentDescription = "Add Journal Entry")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (entries.isEmpty()) {
                Text("No journal entries yet. Tap '+' to add one!")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(items = entries, key = { it.id }) { entry ->
                        JournalEntryCard(
                            entry = entry,
                            modifier = Modifier.combinedClickable(
                                onClick = { onNavigateToEditEntry(entry.id) },
                                onLongClick = { viewModel.onDeletionInitiated(entry) }
                            )
                        )
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.onDeletionCancelled() },
                    title = { Text("Delete Entry?") },
                    text = { Text("Are you sure you want to permanently delete this journal entry?") },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.onDeletionConfirmed() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { viewModel.onDeletionCancelled() }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoodIconDisplay(mood = entry.mood, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.bodyLarge,
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

    Icon(
        imageVector = icon,
        contentDescription = mood,
        modifier = modifier,
        tint = MaterialTheme.colorScheme.secondary
    )
}

private fun formatTimestamp(timestamp: Long): String {
    // Format: "Month day, year, hour:minute AM/PM"
    // Example: "Jun 06, 2025, 10:45 AM"
    val sdf = SimpleDateFormat("MMM dd, yyyy, h:mm a", Locale.getDefault())
    val date = Date(timestamp)
    return sdf.format(date)
}
