package com.jonathon.blossom.ui.habits

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jonathon.blossom.data.DailyHabit
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyHabitsScreen(
    onNavigateToAddHabit: () -> Unit,
    onNavigateToEditHabit: (Int) -> Unit,
    viewModel: DailyHabitsViewModel = hiltViewModel()
) {
    val habits by viewModel.habits.collectAsState(initial = emptyList())
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHabitId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<DailyHabit?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadHabits()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daily Habits",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (habits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No habits yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap the + button to add your first spiritual habit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            HabitCompletionDashboard(
                habits = habits,
                onHabitToggle = { habit ->
                    if (habit.isCompleted) {
                        // Reset completion (allow unchecking)
                        viewModel.resetHabitCompletion(habit)
                    } else {
                        // Complete habit
                        viewModel.completeHabit(habit)
                    }
                },
                onEditClick = onNavigateToEditHabit,
                onDeleteClick = { habit ->
                    habitToDelete = habit
                    showDeleteDialog = true
                },
                onReminderClick = { habitId ->
                    selectedHabitId = habitId
                    showTimePicker = true
                },
                onTestNotification = { habit ->
                    viewModel.testNotification(habit)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { timeInMillis ->
                selectedHabitId?.let { id ->
                    viewModel.updateHabitReminder(id, timeInMillis)
                }
                showTimePicker = false
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { 
                Text(
                    "Delete Habit",
                    style = MaterialTheme.typography.titleLarge
                ) 
            },
            text = { 
                Text(
                    "Are you sure you want to delete this habit?",
                    style = MaterialTheme.typography.bodyMedium
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        habitToDelete?.let { habit ->
                            viewModel.deleteHabit(habit.id)
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DailyHabitCard(
    habit: DailyHabit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row {
                    IconButton(onClick = onReminderClick) {
                        Icon(
                            imageVector = if (habit.reminderTime > 0) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = "Set Reminder",
                            tint = if (habit.reminderTime > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            if (habit.reminderTime > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onReminderClick,
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    Text(
                        text = "Reminder: ${formatTime(habit.reminderTime)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (Long) -> Unit,
    initialTime: Long = System.currentTimeMillis()
) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = initialTime
    }
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }
    var isAm by remember { mutableStateOf(calendar.get(Calendar.AM_PM) == Calendar.AM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Set Reminder Time",
                style = MaterialTheme.typography.titleLarge
            ) 
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Hour",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = selectedHour.toString(),
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { num ->
                                    if (num in 1..12) {
                                        selectedHour = num
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.width(80.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    
                    Text(
                        ":",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Minute",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = String.format("%02d", selectedMinute),
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { num ->
                                    if (num in 0..59) {
                                        selectedMinute = num
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.width(80.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = isAm,
                        onClick = { isAm = true },
                        label = { Text("AM") },
                        leadingIcon = {
                            if (isAm) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    )
                    FilterChip(
                        selected = !isAm,
                        onClick = { isAm = false },
                        label = { Text("PM") },
                        leadingIcon = {
                            if (!isAm) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val hour = when {
                        isAm && selectedHour == 12 -> 0
                        !isAm && selectedHour != 12 -> selectedHour + 12
                        else -> selectedHour
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, selectedMinute)
                    onTimeSelected(calendar.timeInMillis)
                }
            ) {
                Text("Set")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String,
    format: (Int) -> String = { it.toString() }
) {
    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf(format(value)) }
    var isScrolling by remember { mutableStateOf(false) }
    var scrollJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (isEditing) {
            TextField(
                value = textValue,
                onValueChange = { newValue ->
                    textValue = newValue
                    newValue.toIntOrNull()?.let { num ->
                        if (num in range) {
                            onValueChange(num)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        isEditing = false
                        textValue.toIntOrNull()?.let { num ->
                            if (num in range) {
                                onValueChange(num)
                            } else {
                                textValue = format(value)
                            }
                        } ?: run {
                            textValue = format(value)
                        }
                    }
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .width(80.dp)
                    .focusable()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            isEditing = false
                            textValue = format(value)
                        }
                    }
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(80.dp)
                    .height(48.dp)
                    .clickable { isEditing = true }
            ) {
                Text(
                    text = format(value),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.width(80.dp)
        ) {
            IconButton(
                onClick = {
                    if (value > range.first) {
                        onValueChange(value - 1)
                    }
                },
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                isScrolling = true
                                scrollJob = scope.launch {
                                    while (isScrolling) {
                                        if (value > range.first) {
                                            onValueChange(value - 1)
                                        }
                                        delay(100)
                                    }
                                }
                            },
                            onPress = {
                                try {
                                    awaitRelease()
                                } finally {
                                    isScrolling = false
                                    scrollJob?.cancel()
                                }
                            }
                        )
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Increase"
                )
            }
            
            IconButton(
                onClick = {
                    if (value < range.last) {
                        onValueChange(value + 1)
                    }
                },
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                isScrolling = true
                                scrollJob = scope.launch {
                                    while (isScrolling) {
                                        if (value < range.last) {
                                            onValueChange(value + 1)
                                        }
                                        delay(100)
                                    }
                                }
                            },
                            onPress = {
                                try {
                                    awaitRelease()
                                } finally {
                                    isScrolling = false
                                    scrollJob?.cancel()
                                }
                            }
                        )
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Decrease"
                )
            }
        }
    }
}

private fun formatTime(timeInMillis: Long): String {
    val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    return dateFormat.format(Date(timeInMillis))
}

/**
 * 🌟 HABIT COMPLETION DASHBOARD
 * Beautiful interactive dashboard for daily habit tracking
 */
@Composable
fun HabitCompletionDashboard(
    habits: List<DailyHabit>,
    onHabitToggle: (DailyHabit) -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (DailyHabit) -> Unit,
    onReminderClick: (Int) -> Unit,
    onTestNotification: (DailyHabit) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // 📊 Daily Progress Summary
        item {
            HabitProgressSummary(habits = habits)
        }

        // 🎯 Today's Habits
        item {
            Text(
                text = "Today's Habits",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // ✅ Habit Completion Cards
        items(habits, key = { it.id }) { habit ->
            HabitCompletionCard(
                habit = habit,
                onToggle = { onHabitToggle(habit) },
                onEditClick = { onEditClick(habit.id) },
                onDeleteClick = { onDeleteClick(habit) },
                onReminderClick = { onReminderClick(habit.id) },
                onTestNotification = { onTestNotification(habit) }
            )
        }
    }
}

/**
 * 📊 HABIT PROGRESS SUMMARY
 * Shows daily completion percentage and motivation
 */
@Composable
fun HabitProgressSummary(habits: List<DailyHabit>) {
    val completedCount = habits.count { it.isCompleted }
    val totalCount = habits.size
    val completionPercentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress Ring or Emoji
            if (completionPercentage == 100 && totalCount > 0) {
                // Celebration for 100% completion
                val infiniteTransition = rememberInfiniteTransition(label = "celebration")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "celebration_scale"
                )
                
                Text(
                    text = "🎉",
                    fontSize = (32 * scale).sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Perfect Day!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "$completionPercentage%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Daily Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressItem(
                    value = completedCount.toString(),
                    label = "Completed",
                    color = MaterialTheme.colorScheme.primary
                )
                
                ProgressItem(
                    value = (totalCount - completedCount).toString(),
                    label = "Remaining",
                    color = MaterialTheme.colorScheme.outline
                )
                
                ProgressItem(
                    value = habits.maxOfOrNull { it.streakCount }?.toString() ?: "0",
                    label = "Best Streak",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Motivational Message
            if (totalCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = getMotivationalMessage(completionPercentage, completedCount, totalCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 📈 PROGRESS ITEM
 * Individual progress metric
 */
@Composable
fun ProgressItem(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

/**
 * ✅ HABIT COMPLETION CARD
 * Interactive card for completing habits with beautiful animations
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitCompletionCard(
    habit: DailyHabit,
    onToggle: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReminderClick: () -> Unit,
    onTestNotification: (DailyHabit) -> Unit
) {
    // Animation states
    val scale by animateFloatAsState(
        targetValue = if (habit.isCompleted) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    val completionScale by animateFloatAsState(
        targetValue = if (habit.isCompleted) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "completion_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .combinedClickable(
                onClick = onToggle,
                onLongClick = onEditClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isCompleted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (habit.isCompleted) 6.dp else 2.dp
        )
    ) {
        // Beautiful gradient background for completed habits
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = if (habit.isCompleted) {
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    } else {
                        Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ Completion Toggle
                HabitCompletionToggle(
                    isCompleted = habit.isCompleted,
                    onToggle = onToggle,
                    modifier = Modifier.scale(completionScale)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // 📝 Habit Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (habit.isCompleted) {
                            androidx.compose.ui.text.style.TextDecoration.LineThrough
                        } else {
                            androidx.compose.ui.text.style.TextDecoration.None
                        }
                    )

                    if (habit.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = habit.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // 🔥 Streak Display
                    if (habit.streakCount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        StreakDisplay(streak = habit.streakCount)
                    }

                    // ⏰ Reminder Time
                    if (habit.reminderTime > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = formatTime(habit.reminderTime),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // ⚙️ Action Buttons
                Row {
                    IconButton(onClick = onReminderClick) {
                        Icon(
                            imageVector = if (habit.reminderTime > 0) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = "Set Reminder",
                            tint = if (habit.reminderTime > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { onTestNotification(habit) }) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Test Notification",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * ✅ HABIT COMPLETION TOGGLE
 * Beautiful animated completion button
 */
@Composable
fun HabitCompletionToggle(
    isCompleted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = "Toggle completion",
            tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * 🔥 STREAK DISPLAY
 * Beautiful streak visualization with fire emoji
 */
@Composable
fun StreakDisplay(streak: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (streak > 0) {
            // Animated fire emoji for streaks
            val infiniteTransition = rememberInfiniteTransition(label = "fire")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "fire_scale"
            )
            
            Text(
                text = "🔥",
                fontSize = (14 * scale).sp
            )
            Text(
                text = "$streak day${if (streak != 1) "s" else ""} streak",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 💬 MOTIVATIONAL MESSAGES
 * Dynamic messages based on progress
 */
fun getMotivationalMessage(percentage: Int, completed: Int, total: Int): String {
    return when {
        percentage == 100 -> "🌟 Amazing! You've completed all your habits today!"
        percentage >= 80 -> "🚀 You're doing great! Almost there!"
        percentage >= 60 -> "💪 Keep going! You're more than halfway!"
        percentage >= 40 -> "🌱 Good progress! Every step counts!"
        percentage >= 20 -> "🌅 Great start! Keep building momentum!"
        completed > 0 -> "✨ You've started your journey! Keep it up!"
        else -> "🌸 Ready to begin your spiritual practice?"
    }
} 