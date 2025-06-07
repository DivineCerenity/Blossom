package com.example.blossom.ui.habits

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
import com.example.blossom.data.DailyHabit
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
                        "Daily Habits",
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onNavigateToAddHabit) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Habit",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
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
                        "Tap the + button to add your first habit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(habits) { habit ->
                    DailyHabitCard(
                        habit = habit,
                        onEditClick = { onNavigateToEditHabit(habit.id) },
                        onDeleteClick = {
                            habitToDelete = habit
                            showDeleteDialog = true
                        },
                        onReminderClick = {
                            selectedHabitId = habit.id
                            showTimePicker = true
                        }
                    )
                }
            }
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