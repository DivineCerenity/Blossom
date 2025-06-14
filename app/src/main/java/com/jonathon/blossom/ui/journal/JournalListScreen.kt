package com.jonathon.blossom.ui.journal

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import com.jonathon.blossom.ui.components.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import kotlin.math.*

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.jonathon.blossom.R
import com.jonathon.blossom.data.JournalEntry
import com.jonathon.blossom.ui.theme.*
import java.text.SimpleDateFormat
import com.jonathon.blossom.ui.components.HintCard
import com.jonathon.blossom.ui.journal.FullScreenImageViewer
import java.util.*
import java.util.Calendar
// Unused imports can be removed, but these are correct
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.graphicsLayer
import com.jonathon.blossom.ui.components.EntryActionBottomSheet
import com.jonathon.blossom.ui.components.JournalActions

enum class SortOption(val label: String) {
    NEWEST("Newest First"),
    OLDEST("Oldest First"),
    HAPPY_FIRST("Happy First"),
    GRATEFUL_FIRST("Grateful First"),
    EXCITED_FIRST("Excited First"),
    NEUTRAL_FIRST("Neutral First"),
    SAD_FIRST("Sad First")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    var selectedEntry by remember { mutableStateOf<JournalEntry?>(null) }
    var showEntryDetail by remember { mutableStateOf(false) }
    var showImageGallery by remember { mutableStateOf(false) }
    var galleryImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var galleryStartIndex by remember { mutableStateOf(0) }

    // 📱 BOTTOM SHEET STATE
    var showActionBottomSheet by remember { mutableStateOf(false) }
    var selectedEntryForAction by remember { mutableStateOf<JournalEntry?>(null) }

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
                                    SortOption.GRATEFUL_FIRST -> Icons.Default.Favorite
                                    SortOption.EXCITED_FIRST -> Icons.Default.Celebration
                                    SortOption.NEUTRAL_FIRST -> Icons.Default.SentimentNeutral
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
                                                SortOption.GRATEFUL_FIRST -> Icons.Default.Favorite
                                                SortOption.EXCITED_FIRST -> Icons.Default.Celebration
                                                SortOption.NEUTRAL_FIRST -> Icons.Default.SentimentNeutral
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

    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
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

        // Show journal entry detail dialog
        if (showEntryDetail && selectedEntry != null) {
            JournalEntryDetailDialog(
                entry = selectedEntry!!,
                onDismiss = {
                    showEntryDetail = false
                    selectedEntry = null
                },
                onImageClick = { imageUrls, startIndex ->
                    // Use the proper full-screen viewer with swipe navigation
                    galleryImages = imageUrls
                    galleryStartIndex = startIndex
                    showImageGallery = true
                    showEntryDetail = false
                }
            )
        }

        // Show image gallery dialog
        if (showImageGallery && galleryImages.isNotEmpty()) {
            FullScreenImageViewer(
                imageUrls = galleryImages,
                initialImageIndex = galleryStartIndex,
                onDismiss = {
                    showImageGallery = false
                    galleryImages = emptyList()
                    galleryStartIndex = 0
                }
            )
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "No entries match your search"
                            } else {
                                "No journal entries yet"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        if (searchQuery.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap + to create your first entry",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (entries.isNotEmpty()) {
                        item {
HintCard(text = "💡 Tap to view • Long press for options")
                        }
                    }
                    itemsIndexed(
                        items = entries,
                        key = { _, entry -> entry.id }
                    ) { index, entry ->
                        // Clean entry without swipe gestures - bottom sheet handles actions

                        // We use an AnimatedVisibility to remove the item from UI
                        // smoothly when its deletion is initiated.
                        // Entry animation state
                        var isVisible by remember { mutableStateOf(false) }
                        val animationDelay = (index + 1) * 80

                        val slideOffset by animateIntAsState(
                            targetValue = if (isVisible) 0 else 100,
                            animationSpec = tween(
                                durationMillis = 600,
                                delayMillis = animationDelay,
                                easing = FastOutSlowInEasing
                            ),
                            label = "slide_animation"
                        )

                        val alpha by animateFloatAsState(
                            targetValue = if (isVisible) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = 600,
                                delayMillis = animationDelay,
                                easing = FastOutSlowInEasing
                            ),
                            label = "alpha_animation"
                        )

                        LaunchedEffect(Unit) {
                            isVisible = true
                        }

                        AnimatedVisibility(
                            visible = entryToDelete?.id != entry.id,
                            enter = fadeIn(),
                            exit = shrinkOut() + fadeOut()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .graphicsLayer {
                                        translationY = slideOffset.toFloat()
                                        this.alpha = alpha
                                    }
                                    .combinedClickable(
                                        onClick = {
                                            selectedEntry = entry
                                            showEntryDetail = true
                                        },
                                        onLongClick = {
                                            selectedEntryForAction = entry
                                            showActionBottomSheet = true
                                        }
                                    ),
                                    shape = MaterialTheme.shapes.medium,
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    // Beautiful mood-based gradient background
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                brush = getMoodGradient(entry.mood)
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

                                            // Entry streak indicator
                                            val currentStreak = getEntryStreak(entry, entries)
                                            if (currentStreak > 1) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "🔥 Day $currentStreak streak",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }

                                        // Only show image when one exists, but maintain consistent spacing
                                        val displayImageUrl = entry.featuredImageUrl
                                            ?: entry.imageUrl
                                            ?: entry.imageUrls.takeIf { it.isNotEmpty() }?.split("|")?.firstOrNull()

                                        if (displayImageUrl != null) {
                                            Box(
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(MaterialTheme.shapes.medium)
                                            ) {
                                                Image(
                                                    painter = rememberAsyncImagePainter(model = displayImageUrl),
                                                    contentDescription = "Journal Image",
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clickable {
                                                            val imageUrls = entry.imageUrls.split("|").filter { it.isNotBlank() }
                                                            if (imageUrls.isNotEmpty()) {
                                                                galleryImages = imageUrls
                                                                galleryStartIndex = imageUrls.indexOf(displayImageUrl).takeIf { it >= 0 } ?: 0
                                                                showImageGallery = true
                                                            } else {
                                                                fullscreenImageUrl = displayImageUrl
                                                            }
                                                        },
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        } else {
                                            // Invisible spacer to maintain consistent card width
                                            Spacer(modifier = Modifier.size(80.dp))
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                    }

                                    // Seasonal decorations
                                    SeasonalDecorations(
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 📱 JOURNAL ENTRY ACTION BOTTOM SHEET
        selectedEntryForAction?.let { entry ->
            EntryActionBottomSheet(
                isVisible = showActionBottomSheet,
                title = "Journal Entry Actions",
                actions = JournalActions.getActions(
                    onEdit = {
                        onNavigateToEditEntry(entry.id)
                    },
                    onDelete = {
                        viewModel.onDeletionInitiated(entry)
                    }
                ),
                onDismiss = {
                    showActionBottomSheet = false
                    selectedEntryForAction = null
                }
            )
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
        "Happy" -> Color(0xFFFFD700)  // Gold
        "Neutral" -> Color(0xFF90A4AE)  // Blue gray
        "Sad" -> Color(0xFF2196F3)  // Material Blue
        "Excited" -> Color(0xFF66BB6A)  // Green
        "Grateful" -> Color(0xFFE91E63)  // Pink/Red
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(Date(timestamp))
}

// Beautiful theme-reactive mood gradients
@Composable
private fun getMoodGradient(mood: String): Brush {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    return when (mood) {
        "Happy" -> Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.15f),
                Color(0xFFFFD700).copy(alpha = 0.08f), // Golden accent
                surfaceColor.copy(alpha = 0.02f)
            )
        )
        "Grateful" -> Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.12f),
                Color(0xFFE91E63).copy(alpha = 0.06f), // Rose accent
                surfaceColor.copy(alpha = 0.02f)
            )
        )
        "Neutral" -> Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.08f),
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.04f),
                surfaceColor.copy(alpha = 0.02f)
            )
        )
        "Excited" -> Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.14f),
                Color(0xFF66BB6A).copy(alpha = 0.07f), // Green accent
                surfaceColor.copy(alpha = 0.02f)
            )
        )
        "Sad" -> Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.10f),
                Color(0xFF2196F3).copy(alpha = 0.05f), // Blue accent
                surfaceColor.copy(alpha = 0.02f)
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.06f),
                surfaceColor.copy(alpha = 0.02f)
            )
        )
    }
}



// 🔥 PROPER CONSECUTIVE DAY STREAK CALCULATION
private fun getEntryStreak(entry: JournalEntry, allEntries: List<JournalEntry>): Int {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Group entries by date and get sorted unique dates
    val entriesByDate = allEntries
        .groupBy { entryItem ->
            calendar.timeInMillis = entryItem.creationTimestamp
            dateFormat.format(Date(entryItem.creationTimestamp))
        }
        .keys
        .sorted()
        .reversed() // Most recent first

    if (entriesByDate.isEmpty()) return 0

    // Calculate consecutive day streak from most recent entry
    var streak = 0
    val today = dateFormat.format(Date())

    // Start from today or the most recent entry date
    calendar.time = dateFormat.parse(entriesByDate.first()) ?: Date()

    for (dateStr in entriesByDate) {
        val expectedDate = dateFormat.format(calendar.time)

        if (dateStr == expectedDate) {
            streak++
            calendar.add(Calendar.DAY_OF_YEAR, -1) // Go back one day
        } else {
            break // Streak broken
        }
    }

    return streak.coerceAtMost(365) // Cap at 365 for display
}

@Composable
fun BlossomFlowerIcon(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2.2f // Bigger radius to fill more space

        // Draw flower petals (6 petals for more elegance)
        for (i in 0..5) {
            val angle = (i * 60f) * (PI / 180f).toFloat()
            val petalDistance = radius * 0.5f
            val petalCenter = Offset(
                center.x + cos(angle) * petalDistance,
                center.y + sin(angle) * petalDistance
            )

            // Draw larger, more elegant petal as an oval
            drawOval(
                color = color,
                topLeft = Offset(
                    petalCenter.x - radius * 0.35f,
                    petalCenter.y - radius * 0.6f
                ),
                size = Size(radius * 0.7f, radius * 1.2f)
            )
        }

        // Draw elegant center with gradient effect
        drawCircle(
            color = color.copy(alpha = 0.9f),
            radius = radius * 0.25f,
            center = center
        )

        // Add inner center highlight for depth
        drawCircle(
            color = color.copy(alpha = 0.6f),
            radius = radius * 0.15f,
            center = center
        )
    }
}

// Beautiful seasonal decorations
@Composable
fun SeasonalDecorations(modifier: Modifier = Modifier) {
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
    val season = when (currentMonth) {
        2, 3, 4 -> "Spring"    // March, April, May
        5, 6, 7 -> "Summer"    // June, July, August
        8, 9, 10 -> "Autumn"   // September, October, November
        else -> "Winter"       // December, January, February
    }

    Box(modifier = modifier.padding(8.dp)) {
        when (season) {
            "Spring" -> {
                // Cherry blossom petals
                Text(
                    text = "🌸",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.6f)
                )
            }
            "Summer" -> {
                // Sun rays
                Text(
                    text = "☀️",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.5f)
                )
            }
            "Autumn" -> {
                // Falling leaves
                Text(
                    text = "🍂",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.6f)
                )
            }
            "Winter" -> {
                // Snowflakes
                Text(
                    text = "❄️",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.5f)
                )
            }
        }
    }
}

@Composable
fun JournalEntryDetailDialog(
    entry: JournalEntry,
    onDismiss: () -> Unit,
    onImageClick: (List<String>, Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with mood and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MoodIconDisplay(
                        mood = entry.mood,
                        modifier = Modifier.size(32.dp)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date
                Text(
                    text = formatTimestamp(entry.creationTimestamp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Content
                Text(
                    text = entry.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Images if available
                val imageUrls = entry.imageUrls.split("|").filter { it.isNotBlank() }
                if (imageUrls.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(imageUrls) { index, imageUrl ->
                            Card(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable { onImageClick(imageUrls, index) },
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(imageUrl),
                                    contentDescription = "Journal image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    if (imageUrls.size > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${imageUrls.size} photos • Tap to view full screen",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
