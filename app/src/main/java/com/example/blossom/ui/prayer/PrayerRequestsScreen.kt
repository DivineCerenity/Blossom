package com.example.blossom.ui.prayer

import androidx.compose.animation.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.blossom.ui.components.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.blossom.data.PrayerCategory
import com.example.blossom.data.PrayerPriority
import com.example.blossom.data.PrayerRequest
import com.example.blossom.ui.components.HintCard
import java.text.SimpleDateFormat
import java.util.*
import com.example.blossom.ui.prayer.PrayerSortOption
import com.example.blossom.ui.prayer.EditPrayerRequestDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import com.example.blossom.ui.components.EntryActionBottomSheet
import com.example.blossom.ui.components.PrayerActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.blossom.ui.components.AchievementCelebrationManager
import com.example.blossom.ui.dashboard.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PrayerRequestsScreen(
    viewModel: PrayerRequestsViewModel = hiltViewModel(),
    addPrayerTrigger: Boolean = false,
    onAddPrayerTriggered: () -> Unit = {}
) {
    // 📖 DAILY VERSE INTEGRATION
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val newAchievements by viewModel.newAchievements.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    // Handle add prayer trigger from navigation
    LaunchedEffect(addPrayerTrigger) {
        if (addPrayerTrigger) {
            showAddDialog = true
            onAddPrayerTriggered()
        }
    }

    // 📖 REFRESH VERSE WHEN SCREEN BECOMES VISIBLE
    LaunchedEffect(Unit) {
        dashboardViewModel.fetchVerse()
    }
    
    var selectedSortOption by remember { mutableStateOf(PrayerSortOption.NEWEST_FIRST) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var editingPrayerRequest by remember { mutableStateOf<PrayerRequest?>(null) }
    var prayerToDelete by remember { mutableStateOf<PrayerRequest?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 📱 BOTTOM SHEET STATE
    var showActionBottomSheet by remember { mutableStateOf(false) }
    var selectedPrayerForAction by remember { mutableStateOf<PrayerRequest?>(null) }

    Scaffold(
        topBar = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search prayer requests...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    Box {
                        IconButton(onClick = { sortMenuExpanded = true }) {
                            Icon(
                                imageVector = when (selectedSortOption) {
                                    PrayerSortOption.PRIORITY_HIGH_FIRST -> Icons.Default.KeyboardArrowDown
                                    PrayerSortOption.PRIORITY_LOW_FIRST -> Icons.Default.KeyboardArrowUp
                                    PrayerSortOption.NEWEST_FIRST -> Icons.Default.ArrowDownward
                                    PrayerSortOption.OLDEST_FIRST -> Icons.Default.ArrowUpward
                                    PrayerSortOption.CATEGORY_A_TO_Z -> Icons.Default.SortByAlpha
                                    PrayerSortOption.CATEGORY_Z_TO_A -> Icons.Default.SortByAlpha
                                    PrayerSortOption.ANSWERED_FIRST -> Icons.Default.CheckCircle
                                    PrayerSortOption.UNANSWERED_FIRST -> Icons.Default.RadioButtonUnchecked
                                },
                                contentDescription = "Sort"
                            )
                        }

                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            PrayerSortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        selectedSortOption = option
                                        sortMenuExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when (option) {
                                                PrayerSortOption.PRIORITY_HIGH_FIRST -> Icons.Default.KeyboardArrowDown
                                                PrayerSortOption.PRIORITY_LOW_FIRST -> Icons.Default.KeyboardArrowUp
                                                PrayerSortOption.NEWEST_FIRST -> Icons.Default.ArrowDownward
                                                PrayerSortOption.OLDEST_FIRST -> Icons.Default.ArrowUpward
                                                PrayerSortOption.CATEGORY_A_TO_Z -> Icons.Default.SortByAlpha
                                                PrayerSortOption.CATEGORY_Z_TO_A -> Icons.Default.SortByAlpha
                                                PrayerSortOption.ANSWERED_FIRST -> Icons.Default.CheckCircle
                                                PrayerSortOption.UNANSWERED_FIRST -> Icons.Default.RadioButtonUnchecked
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 📖 DAILY VERSE CARD (MOVED FROM HOME!)
            DailyVerseCard(
                dashboardViewModel = dashboardViewModel,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Stats Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    title = "Active",
                    count = uiState.activePrayerCount,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    title = "Answered",
                    count = uiState.answeredPrayerCount,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Answered") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("All") }
                )
            }

            // Prayer Requests List
            val prayerRequests = when (selectedTab) {
                0 -> uiState.activePrayerRequests
                1 -> uiState.answeredPrayerRequests
                else -> uiState.allPrayerRequests
            }
            // Filter and sort logic
            val filteredPrayerRequests = prayerRequests.filter { prayer ->
                searchQuery.isEmpty() ||
                prayer.title.contains(searchQuery, ignoreCase = true) ||
                prayer.description.contains(searchQuery, ignoreCase = true) ||
                prayer.category.displayName.contains(searchQuery, ignoreCase = true)
            }

            val sortedPrayerRequests = when (selectedSortOption) {
                PrayerSortOption.PRIORITY_HIGH_FIRST -> filteredPrayerRequests.sortedByDescending { it.priority }
                PrayerSortOption.PRIORITY_LOW_FIRST -> filteredPrayerRequests.sortedBy { it.priority }
                PrayerSortOption.NEWEST_FIRST -> filteredPrayerRequests.sortedByDescending { it.createdDate }
                PrayerSortOption.OLDEST_FIRST -> filteredPrayerRequests.sortedBy { it.createdDate }
                PrayerSortOption.CATEGORY_A_TO_Z -> filteredPrayerRequests.sortedBy { it.category.displayName }
                PrayerSortOption.CATEGORY_Z_TO_A -> filteredPrayerRequests.sortedByDescending { it.category.displayName }
                PrayerSortOption.ANSWERED_FIRST -> filteredPrayerRequests.sortedByDescending { it.isAnswered }
                PrayerSortOption.UNANSWERED_FIRST -> filteredPrayerRequests.sortedBy { it.isAnswered }
            }

            if (sortedPrayerRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "No prayers match your search"
                            } else {
                                when (selectedTab) {
                                    0 -> "No active prayer requests"
                                    1 -> "No answered prayers yet"
                                    else -> "No prayer requests"
                                }
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        if (searchQuery.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap the + button to add your first prayer request",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (sortedPrayerRequests.isNotEmpty()) {
                        item {
                            HintCard(text = "💡 Long press for actions")
                        }
                    }
                    itemsIndexed(sortedPrayerRequests, key = { _, prayer -> prayer.id }) { index, prayerRequest ->
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

                        // Clean prayer card with animation - bottom sheet handles actions
                        PrayerRequestCard(
                            prayerRequest = prayerRequest,
                            modifier = Modifier
                                .graphicsLayer {
                                    translationY = slideOffset.toFloat()
                                    this.alpha = alpha
                                }
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = {
                                        selectedPrayerForAction = prayerRequest
                                        showActionBottomSheet = true
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
    
    // Add Prayer Request Dialog
    if (showAddDialog) {
        AddPrayerRequestDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, description, category, priority ->
                viewModel.addPrayerRequest(title, description, category, priority)
                showAddDialog = false
            }
        )
    }

    // Edit Prayer Request Dialog
    editingPrayerRequest?.let { request ->
        EditPrayerRequestDialog(
            prayerRequest = request,
            onDismiss = { editingPrayerRequest = null },
            onSave = { title, description, category, priority ->
                viewModel.updatePrayerRequest(
                    request.copy(
                        title = title,
                        description = description,
                        category = category,
                        priority = priority
                    )
                )
                editingPrayerRequest = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && prayerToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; prayerToDelete = null },
            title = { Text("Delete Prayer Request") },
            text = { Text("Are you sure you want to delete this prayer request?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePrayerRequest(prayerToDelete!!)
                    showDeleteDialog = false
                    prayerToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; prayerToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // 📱 PRAYER REQUEST ACTION BOTTOM SHEET
    selectedPrayerForAction?.let { prayer ->
        EntryActionBottomSheet(
            isVisible = showActionBottomSheet,
            title = "Prayer Request Actions",
            actions = PrayerActions.getActions(
                isAnswered = prayer.isAnswered,
                onEdit = {
                    editingPrayerRequest = prayer
                },
                onDelete = {
                    prayerToDelete = prayer
                    showDeleteDialog = true
                },
                onToggleAnswered = {
                    viewModel.toggleAnswered(prayer)
                }
            ),
            onDismiss = {
                showActionBottomSheet = false
                selectedPrayerForAction = null
            }
        )
    }

    // 🏆 ACHIEVEMENT CELEBRATION
    if (newAchievements.isNotEmpty()) {
        AchievementCelebrationManager(
            achievements = newAchievements,
            onAllDismissed = {
                viewModel.clearAchievements()
            }
        )
    }
}

@Composable
fun StatsCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerRequestCard(
    prayerRequest: PrayerRequest,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        // Beautiful theme-based gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = getPrayerGradient(prayerRequest.priority, prayerRequest.isAnswered)
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prayerRequest.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (prayerRequest.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prayerRequest.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { },
                        label = { Text(prayerRequest.category.displayName) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(getPriorityColor(prayerRequest.priority))
                    )
                }
                Text(
                    text = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
                        .format(Date(prayerRequest.createdDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        }
    }
}

@Composable
fun getPriorityColor(priority: PrayerPriority): Color {
    return when (priority) {
        PrayerPriority.LOW -> com.example.blossom.ui.theme.PriorityLow      // Sage green
        PrayerPriority.MEDIUM -> com.example.blossom.ui.theme.PriorityMedium  // Warm amber
        PrayerPriority.HIGH -> com.example.blossom.ui.theme.PriorityHigh     // Soft red
        PrayerPriority.URGENT -> com.example.blossom.ui.theme.PriorityUrgent  // Deeper red
    }
}

// Beautiful theme-reactive prayer gradients
@Composable
private fun getPrayerGradient(priority: PrayerPriority, isAnswered: Boolean): androidx.compose.ui.graphics.Brush {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val priorityColor = getPriorityColor(priority)

    return if (isAnswered) {
        // Special gradient for answered prayers - peaceful and celebratory
        androidx.compose.ui.graphics.Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
                primaryColor.copy(alpha = 0.08f),
                surfaceColor.copy(alpha = 0.02f)
            )
        )
    } else {
        // Priority-based gradients that adapt to theme
        when (priority) {
            PrayerPriority.URGENT -> androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.18f),
                    priorityColor.copy(alpha = 0.12f), // Deeper red accent
                    surfaceColor.copy(alpha = 0.02f)
                )
            )
            PrayerPriority.HIGH -> androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.15f),
                    priorityColor.copy(alpha = 0.10f), // Soft red accent
                    surfaceColor.copy(alpha = 0.02f)
                )
            )
            PrayerPriority.MEDIUM -> androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.12f),
                    priorityColor.copy(alpha = 0.08f), // Warm amber accent
                    surfaceColor.copy(alpha = 0.02f)
                )
            )
            PrayerPriority.LOW -> androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.10f),
                    priorityColor.copy(alpha = 0.06f), // Sage green accent
                    surfaceColor.copy(alpha = 0.02f)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPrayerRequestDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, PrayerCategory, PrayerPriority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PrayerCategory.PERSONAL) }
    var selectedPriority by remember { mutableStateOf(PrayerPriority.MEDIUM) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showPriorityDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Prayer Request") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        PrayerCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = showPriorityDropdown,
                    onExpandedChange = { showPriorityDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedPriority.displayName,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPriorityDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = showPriorityDropdown,
                        onDismissRequest = { showPriorityDropdown = false }
                    ) {
                        PrayerPriority.values().forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority.displayName) },
                                onClick = {
                                    selectedPriority = priority
                                    showPriorityDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, description, selectedCategory, selectedPriority)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * 📖 DAILY VERSE CARD
 * Beautiful collapsible daily verse integration in Prayer section
 */
@Composable
fun DailyVerseCard(
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        tonalElevation = 4.dp
    ) {
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                slideInVertically() + fadeIn() togetherWith slideOutVertically() + fadeOut()
            },
            label = "verse_expansion"
        ) { expanded ->
            Column(modifier = Modifier.padding(16.dp)) {
                // Header row (always visible)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📖",
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (expanded) "Verse of the Day" else uiState.verseReference,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Compact preview or full verse
                when {
                    uiState.isLoading -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Loading verse...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    uiState.error != null -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Unable to load verse",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    expanded -> {
                        // Full verse when expanded
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "\"${uiState.verseText}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.verseReference,
                            modifier = Modifier.align(Alignment.End),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    else -> {
                        // Compact preview when collapsed
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${uiState.verseText.take(50)}${if (uiState.verseText.length > 50) "..." else ""}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}