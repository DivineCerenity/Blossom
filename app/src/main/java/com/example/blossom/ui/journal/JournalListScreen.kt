package com.example.blossom.ui.journal

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.blossom.data.JournalEntry
import com.example.blossom.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
// Unused imports can be removed, but these are correct
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.graphicsLayer

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
    val isLoading by viewModel.isLoading.collectAsState(initial = false)

    // State for fullscreen image dialog
    var fullscreenImageUrl by remember { mutableStateOf<String?>(null) }

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
        val entryToDelete by viewModel.entryToDelete.collectAsState(initial = null)

        // Show the confirmation dialog if an entry is marked for deletion
        if (entryToDelete != null) {
            AlertDialog(
                onDismissRequest = { viewModel.onDeletionCancelled() },
                title = { Text("Delete Entry") },
                text = { Text("Are you sure you want to delete this entry?") },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.onDeletionConfirmed() }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onDeletionCancelled() }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Show fullscreen image dialog if needed
        fullscreenImageUrl?.let { imageUrl ->
            ZoomableImageDialog(imageUrl = imageUrl, onDismiss = { fullscreenImageUrl = null })
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            entries.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No entries match your search" else "No journal entries yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(
                        items = entries,
                        key = { it.id }
                    ) { entry ->
                        // This state is correctly defined inside the 'items' scope,
                        // ensuring each item has its own dismiss state.
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { swipeValue ->
                                when (swipeValue) {
                                    // Swipe right-to-left to delete
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        viewModel.onDeletionInitiated(entry)
                                        // Return false to prevent immediate dismissal.
                                        // The item will be removed when the dialog is confirmed.
                                        false
                                    }
                                    // Swipe left-to-right to edit
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        onNavigateToEditEntry(entry.id)
                                        // Return false to snap the item back after navigating.
                                        false
                                    }
                                    SwipeToDismissBoxValue.Settled -> false
                                }
                            }
                        )

                        // We use an AnimatedVisibility to remove the item from UI
                        // smoothly when its deletion is initiated.
                        AnimatedVisibility(
                            visible = entryToDelete?.id != entry.id,
                            enter = fadeIn(),
                            exit = shrinkOut() + fadeOut()
                        ) {
                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val direction = dismissState.targetValue
                                    val color by animateColorAsState(
                                        targetValue = when (direction) {
                                            SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                            SwipeToDismissBoxValue.Settled -> Color.Transparent
                                        },
                                        label = "Swipe background color"
                                    )
                                    val icon = when (direction) {
                                        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                                        SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                                        SwipeToDismissBoxValue.Settled -> null
                                    }
                                    val alignment = when(direction) {
                                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                        SwipeToDismissBoxValue.Settled -> Alignment.Center // Won't be visible
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = alignment
                                    ) {
                                        icon?.let {
                                            Icon(
                                                imageVector = it,
                                                contentDescription = if (direction == SwipeToDismissBoxValue.StartToEnd) "Edit" else "Delete",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            ) { // <-- CORRECT SYNTAX: Content is in the trailing lambda
                                Card(
                                    modifier = Modifier
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
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(MaterialTheme.shapes.small)
                                                .background(
                                                    brush = Brush.linearGradient(
                                                        colors = listOf(
                                                            getMoodColor(entry.mood).copy(alpha = 0.2f),
                                                            getMoodColor(entry.mood).copy(alpha = 0.1f)
                                                        )
                                                    )
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

                                        // Example: Show gallery image if present
                                        // Replace 'entry.imageUrl' with your actual image property
                                        if (entry.imageUrl != null) {
                                            Image(
                                                painter = rememberAsyncImagePainter(model = entry.imageUrl),
                                                contentDescription = "Journal Image",
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(MaterialTheme.shapes.medium)
                                                    .clickable { fullscreenImageUrl = entry.imageUrl },
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Zoomable fullscreen image dialog
@Composable
fun ZoomableImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        var scale by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 5f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
                .clickable { onDismiss() }
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                contentScale = ContentScale.Fit
            )
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
        tint = getMoodColor(mood = mood)
    )
}

@Composable
private fun getMoodColor(mood: String): Color {
    return when (mood) {
        "Happy" -> HappyColor
        "Neutral" -> NeutralColor
        "Sad" -> SadColor
        "Excited" -> ExcitedColor
        "Grateful" -> GratefulColor
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
}
