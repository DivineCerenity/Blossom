package com.example.blossom.ui.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.blossom.data.PrayerCategory
import com.example.blossom.data.PrayerPriority
import com.example.blossom.data.PrayerRequest
import java.text.SimpleDateFormat
import java.util.*
import com.example.blossom.ui.prayer.PrayerSortOption
import com.example.blossom.ui.prayer.EditPrayerRequestDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PrayerRequestsScreen(
    viewModel: PrayerRequestsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    
    var selectedSortOption by remember { mutableStateOf(PrayerSortOption.NEWEST_FIRST) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var editingPrayerRequest by remember { mutableStateOf<PrayerRequest?>(null) }
    var prayerToDelete by remember { mutableStateOf<PrayerRequest?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Prayer Request")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats Cards and Sort Dropdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
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
                Box {
                    IconButton(onClick = { sortMenuExpanded = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
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
                                }
                            )
                        }
                    }
                }
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
            // Sort logic
            val sortedPrayerRequests = when (selectedSortOption) {
                PrayerSortOption.PRIORITY_HIGH_FIRST -> prayerRequests.sortedByDescending { it.priority }
                PrayerSortOption.PRIORITY_LOW_FIRST -> prayerRequests.sortedBy { it.priority }
                PrayerSortOption.NEWEST_FIRST -> prayerRequests.sortedByDescending { it.createdDate }
                PrayerSortOption.OLDEST_FIRST -> prayerRequests.sortedBy { it.createdDate }
                PrayerSortOption.CATEGORY_A_TO_Z -> prayerRequests.sortedBy { it.category.displayName }
                PrayerSortOption.CATEGORY_Z_TO_A -> prayerRequests.sortedByDescending { it.category.displayName }
                PrayerSortOption.ANSWERED_FIRST -> prayerRequests.sortedByDescending { it.isAnswered }
                PrayerSortOption.UNANSWERED_FIRST -> prayerRequests.sortedBy { it.isAnswered }
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
                            text = when (selectedTab) {
                                0 -> "No active prayer requests"
                                1 -> "No answered prayers yet"
                                else -> "No prayer requests"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the + button to add your first prayer request",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedPrayerRequests, key = { it.id }) { prayerRequest ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value: SwipeToDismissBoxValue ->
                                when (value) {
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        prayerToDelete = prayerRequest
                                        showDeleteDialog = true
                                        false // Wait for confirmation
                                    }
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        editingPrayerRequest = prayerRequest
                                        false // Don't auto-dismiss
                                    }
                                    else -> false
                                }
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {}, // Optionally add background visuals
                            modifier = Modifier,
                            content = {
                                PrayerRequestCard(
                                    prayerRequest = prayerRequest,
                                    modifier = Modifier.combinedClickable(
                                        onClick = {},
                                        onLongClick = { viewModel.toggleAnswered(prayerRequest) }
                                    )
                                )
                            }
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
            containerColor = if (prayerRequest.isAnswered) {
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
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
                    text = SimpleDateFormat("MMM dd", Locale.getDefault())
                        .format(Date(prayerRequest.createdDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
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