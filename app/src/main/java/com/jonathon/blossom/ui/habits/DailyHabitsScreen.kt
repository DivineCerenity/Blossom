package com.jonathon.blossom.ui.habits

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.ui.draw.scale
import com.jonathon.blossom.ui.components.AchievementCelebrationManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.jonathon.blossom.ui.components.EntryActionBottomSheet
import com.jonathon.blossom.ui.components.HintCard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DailyHabitsScreen(
    onNavigateToAddHabit: () -> Unit,
    onNavigateToEditHabit: (Int) -> Unit,
    viewModel: DailyHabitsViewModel = hiltViewModel()
) {
    val habits by viewModel.habits.collectAsState(initial = emptyList())
    val newAchievements by viewModel.newAchievements.collectAsState() // 🏆 Achievement celebration
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHabitId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<DailyHabit?>(null) }
    var showActionBottomSheet by remember { mutableStateOf(false) }
    var selectedHabitForAction by remember { mutableStateOf<DailyHabit?>(null) }
    var showDescriptionDialog by remember { mutableStateOf(false) }
    var selectedHabitForDescription by remember { mutableStateOf<DailyHabit?>(null) }

    // 📱 SCROLL STATE MANAGEMENT - Always start at top when navigating to this screen
    val listState = rememberLazyListState()
    
    // Reset scroll position to top when screen is navigated to
    LaunchedEffect(Unit) {
        listState.animateScrollToItem(0)
    }

    LaunchedEffect(Unit) {
        viewModel.loadHabits()
    }

    Scaffold(
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
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    // Animated floating effect for the icon
                    val infiniteTransition = rememberInfiniteTransition(label = "empty_state")
                    val offsetY by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = -10f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "float_animation"
                    )
                    
                    // Beautiful gradient background circle
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .offset(y = offsetY.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                    )
                                ),
                                shape = RoundedCornerShape(60.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🌱",
                            fontSize = 48.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        "Begin Your Journey",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Create your first spiritual habit and watch your faith blossom",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
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
                onHabitClick = { habit ->
                    selectedHabitForDescription = habit
                    showDescriptionDialog = true
                },
                onHabitLongPress = { habit ->
                    selectedHabitForAction = habit
                    showActionBottomSheet = true
                },
                listState = listState,
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

    // 📱 HABIT ACTION BOTTOM SHEET
    selectedHabitForAction?.let { habit ->
        EntryActionBottomSheet(
            isVisible = showActionBottomSheet,
            title = "Habit Actions",
            actions = HabitActions.getActions(
                hasReminder = habit.reminderTime > 0,
                onEdit = {
                    onNavigateToEditHabit(habit.id)
                },
                onDelete = {
                    habitToDelete = habit
                    showDeleteDialog = true
                },
                onSetReminder = {
                    selectedHabitId = habit.id
                    showTimePicker = true
                }
            ),
            onDismiss = {
                showActionBottomSheet = false
                selectedHabitForAction = null
            }
        )
    }

    // 📖 HABIT DESCRIPTION DIALOG
    selectedHabitForDescription?.let { habit ->
        HabitDescriptionDialog(
            habit = habit,
            isVisible = showDescriptionDialog,
            onDismiss = {
                showDescriptionDialog = false
                selectedHabitForDescription = null
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

    // 🏆 HABIT ACHIEVEMENT CELEBRATION
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
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = initialTime } }

    LaunchedEffect(Unit) {
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                onTimeSelected(calendar.timeInMillis)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )

        timePickerDialog.setOnDismissListener { onDismiss() }
        timePickerDialog.show()
    }
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
    onHabitClick: (DailyHabit) -> Unit,
    onHabitLongPress: (DailyHabit) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {

    LazyColumn(
        state = listState, // Attach the scroll state to the LazyColumn
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
    ) {
        // 📊 Daily Progress Summary
        item {
            HabitProgressSummary(habits = habits)
        }

        // 💡 Hint Card
        if (habits.isNotEmpty()) {
            item {
                HintCard(text = "💡 Tap to view • Long press for options")
            }
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
                onClick = { onHabitClick(habit) },
                onLongPress = { onHabitLongPress(habit) }
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
                .padding(12.dp),
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
                    fontSize = (24 * scale).sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Text(
                    text = "Perfect Day!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                // Circular Progress Ring
                val animatedProgress by animateFloatAsState(
                    targetValue = completionPercentage / 100f,
                    animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
                    label = "progress_animation"
                )
                
                // Get theme colors outside of Canvas
                val primaryColor = MaterialTheme.colorScheme.primary
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    Canvas(modifier = Modifier.size(80.dp)) {
                        val strokeWidth = 8.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val center = Offset(size.width / 2, size.height / 2)
                        
                        // Background circle
                        drawCircle(
                            color = Color.Gray.copy(alpha = 0.2f),
                            radius = radius,
                            center = center,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )
                        
                        // Progress arc
                        if (animatedProgress > 0) {
                            drawArc(
                                color = primaryColor,
                                startAngle = -90f,
                                sweepAngle = 360f * animatedProgress,
                                useCenter = false,
                                topLeft = Offset(
                                    center.x - radius,
                                    center.y - radius
                                ),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                    }
                    
                    Text(
                        text = "$completionPercentage%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Daily Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                Spacer(modifier = Modifier.height(8.dp))
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
    onClick: () -> Unit,
    onLongPress: () -> Unit
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
                onClick = onClick,
                onLongClick = onLongPress
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (habit.isCompleted) 6.dp else 2.dp
        )
    ) {
        // Beautiful gradient background for all habits
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = getHabitGradient(habit.isCompleted, habit.streakCount),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
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
                        val shortDescription = habit.description.split(" ").take(6).joinToString(" ") + if (habit.description.split(" ").size > 6) "..." else ""
                        Text(
                            text = shortDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
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
    // Completion animation with bounce
    val completionScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.0f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "completion_bounce"
    )
    
    // Color animation
    val iconColor by animateColorAsState(
        targetValue = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = tween(300),
        label = "icon_color"
    )
    
    // Rotation animation for completion
    val rotation by animateFloatAsState(
        targetValue = if (isCompleted) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "completion_rotation"
    )
    
    IconButton(
        onClick = onToggle,
        modifier = modifier.scale(completionScale)
    ) {
        Icon(
            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = "Toggle completion",
            tint = iconColor,
            modifier = Modifier
                .size(32.dp)
                .graphicsLayer {
                    rotationZ = if (isCompleted) rotation else 0f
                }
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

/**
 * 🎯 HABIT ACTIONS
 * Actions available for habit entries in bottom sheet
 */
object HabitActions {
    fun getActions(
        hasReminder: Boolean,
        onEdit: () -> Unit,
        onDelete: () -> Unit,
        onSetReminder: () -> Unit
    ): List<com.jonathon.blossom.ui.components.BottomSheetAction> = listOf(
        com.jonathon.blossom.ui.components.BottomSheetAction(
            title = "Edit Habit",
            subtitle = "Modify this habit",
            icon = Icons.Default.Edit,
            type = com.jonathon.blossom.ui.components.ActionType.PRIMARY,
            onClick = onEdit
        ),
        com.jonathon.blossom.ui.components.BottomSheetAction(
            title = if (hasReminder) "Update Reminder" else "Set Reminder",
            subtitle = if (hasReminder) "Change reminder time" else "Add a daily reminder",
            icon = if (hasReminder) Icons.Default.Notifications else Icons.Default.NotificationsOff,
            type = com.jonathon.blossom.ui.components.ActionType.PRIMARY,
            onClick = onSetReminder
        ),
        com.jonathon.blossom.ui.components.BottomSheetAction(
            title = "Delete Habit",
            subtitle = "Remove this habit permanently",
            icon = Icons.Default.Delete,
            type = com.jonathon.blossom.ui.components.ActionType.DESTRUCTIVE,
            onClick = onDelete
        )
    )
}

/**
 * 📖 HABIT DESCRIPTION DIALOG
 * Shows full habit details when tapped
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDescriptionDialog(
    habit: DailyHabit,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    if (habit.description.isNotBlank()) {
                        Text(
                            text = habit.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Text(
                            text = "No description provided for this habit.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }

                    // Show additional details
                    Spacer(modifier = Modifier.height(16.dp))

                    if (habit.reminderTime > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Reminder: ${formatTime(habit.reminderTime)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (habit.streakCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🔥",
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${habit.streakCount} day${if (habit.streakCount != 1) "s" else ""} streak",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.large
        )
    }
}

/**
 * 🎨 HABIT GRADIENT SYSTEM
 * Beautiful theme-reactive gradients matching prayer card style
 * Streak & Completion gradient hierarchy:
 * - Not completed: Subtle, encouraging gradient
 * - Completed today (no streak): Gentle celebration gradient
 * - Small streak (2-4 days): Warm, motivating gradient
 * - Medium streak (5-6 days): Achievement-focused gradient
 * - Amazing streak (7+ days): Special celebratory gradient
 */
@Composable
private fun getHabitGradient(isCompleted: Boolean, streakCount: Int): Brush {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiaryContainer
    val surfaceColor = MaterialTheme.colorScheme.surface

    return if (isCompleted) {
        // Completion gradients with streak-based intensity
        when {
            streakCount >= 7 -> {
                // Amazing streak! Special celebratory gradient (like answered prayers)
                Brush.linearGradient(
                    colors = listOf(
                        tertiaryColor.copy(alpha = 0.30f),
                        primaryColor.copy(alpha = 0.25f),
                        Color(0xFFFFD700).copy(alpha = 0.15f), // Gold accent
                        primaryColor.copy(alpha = 0.20f),
                        surfaceColor.copy(alpha = 0.05f)
                    )
                )
            }
            streakCount >= 5 -> {
                // Great streak! Enhanced achievement gradient
                Brush.linearGradient(
                    colors = listOf(
                        secondaryColor.copy(alpha = 0.25f),
                        primaryColor.copy(alpha = 0.20f),
                        tertiaryColor.copy(alpha = 0.15f),
                        secondaryColor.copy(alpha = 0.18f),
                        surfaceColor.copy(alpha = 0.05f)
                    )
                )
            }
            streakCount >= 2 -> {
                // Building streak! Motivating gradient
                Brush.linearGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.22f),
                        secondaryColor.copy(alpha = 0.18f),
                        primaryColor.copy(alpha = 0.15f),
                        surfaceColor.copy(alpha = 0.05f)
                    )
                )
            }
            else -> {
                // Completed today! Gentle celebration gradient
                Brush.linearGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.20f),
                        tertiaryColor.copy(alpha = 0.15f),
                        primaryColor.copy(alpha = 0.12f),
                        surfaceColor.copy(alpha = 0.05f)
                    )
                )
            }
        }
    } else {
        // Not completed - Subtle, encouraging gradient
        Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.15f),
                MaterialTheme.colorScheme.outline.copy(alpha = 0.10f),
                primaryColor.copy(alpha = 0.08f),
                surfaceColor.copy(alpha = 0.05f)
            )
        )
    }
}