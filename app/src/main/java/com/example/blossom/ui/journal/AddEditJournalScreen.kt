package com.example.blossom.ui.journal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions // <--- THIS IS THE FIX
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJournalScreen(
    viewModel: JournalViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val cameraImageUriState = remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Copy the image to app storage for persistence
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val destFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "journal_gallery_${System.currentTimeMillis()}.jpg"
            )
            inputStream?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            val destUri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                destFile
            )
            viewModel.onImageUriChanged(destUri.toString())
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && cameraImageUriState.value != null) {
            viewModel.onImageUriChanged(cameraImageUriState.value.toString())
        }
    }
    fun launchCamera() {
        val photoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "journal_${System.currentTimeMillis()}.jpg"
        )
        val uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            photoFile
        )
        cameraImageUriState.value = uri
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            onNavigateBack()
            viewModel.eventHandled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Entry" else "New Entry") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.saveJournalEntry() }) {
                Icon(Icons.Default.Done, contentDescription = "Save Entry")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChanged(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.content,
                onValueChange = { viewModel.onContentChanged(it) },
                label = { Text("What's on your mind?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("How are you feeling?", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            MoodSelector(
                selectedMood = uiState.selectedMood,
                onMoodSelected = { viewModel.onMoodSelected(it) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Add a photo", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { galleryLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Image, contentDescription = "Pick from gallery")
                    Spacer(Modifier.width(4.dp))
                    Text("Gallery")
                }
                Button(onClick = { launchCamera() }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Take photo")
                    Spacer(Modifier.width(4.dp))
                    Text("Camera")
                }
            }
            val displayImageUri = uiState.imageUri?.let { Uri.parse(it) }
            displayImageUri?.let { uri ->
                Spacer(Modifier.height(12.dp))
                AsyncImage(
                    model = uri,
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.deleteImage() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete image")
                    Spacer(Modifier.width(4.dp))
                    Text("Delete Image")
                }
            }
        }
    }
}

@Composable
fun MoodSelector(
    selectedMood: String,
    onMoodSelected: (String) -> Unit
) {
    val moods = listOf("Happy", "Neutral", "Sad", "Excited", "Grateful")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        moods.forEach { mood ->
            val icon = when (mood) {
                "Happy" -> Icons.Default.SentimentVerySatisfied
                "Neutral" -> Icons.Default.SentimentNeutral
                "Sad" -> Icons.Default.SentimentVeryDissatisfied
                "Excited" -> Icons.Default.Celebration
                "Grateful" -> Icons.Default.Favorite
                else -> Icons.AutoMirrored.Filled.HelpOutline
            }
            MoodIcon(
                icon = icon,
                label = mood,
                isSelected = (mood == selectedMood),
                onClick = { onMoodSelected(mood) }
            )
        }
    }
}

@Composable
fun MoodIcon(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(40.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}