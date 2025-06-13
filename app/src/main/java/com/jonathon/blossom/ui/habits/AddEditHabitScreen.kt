package com.jonathon.blossom.ui.habits

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jonathon.blossom.data.DailyHabit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    habitId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: DailyHabitsViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Load existing habit data if editing
    LaunchedEffect(habitId) {
        if (habitId != null) {
            viewModel.getHabitById(habitId)?.let { habit ->
                title = habit.title
                description = habit.description
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (habitId == null) "Add Habit" else "Edit Habit",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Habit Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        if (habitId == null) {
                            // Add new habit
                            val newHabit = DailyHabit(
                                title = title.trim(),
                                description = description.trim(),
                                isCompleted = false,
                                isEnabled = true,
                                streakCount = 0,
                                lastCompletedDate = 0,
                                reminderTime = 0
                            )
                            viewModel.addHabit(newHabit)
                        } else {
                            // Update existing habit - we need to use the current state
                            // Since we already loaded the habit data, we can create the update directly
                            viewModel.updateHabitById(
                                habitId = habitId,
                                newTitle = title.trim(),
                                newDescription = description.trim()
                            )
                        }
                        onNavigateBack()
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (habitId == null) "Add Habit" else "Save Changes")
            }
        }
    }
} 