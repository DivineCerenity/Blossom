package com.example.blossom.ui.journal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.request.ImageRequest
import android.content.ContentValues
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddEditJournalScreen(
    uiState: AddEditJournalUiState,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onMoodSelected: (String) -> Unit,
    onImageUriChanged: (String?) -> Unit,
    onAddImage: (String) -> Unit,
    onDeleteImage: (String) -> Unit,
    onSetFeaturedImage: (String) -> Unit,
    saveJournalEntry: () -> Unit,
    eventHandled: () -> Unit,
    onNavigateBack: () -> Unit, // <-- Add this parameter
    isEditing: Boolean = false // <-- Add a default value if not provided by the caller
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val cameraImageUriState = remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showFullScreenViewer by remember { mutableStateOf(false) }
    var fullScreenImageIndex by remember { mutableIntStateOf(0) }

    // Save to gallery function
    fun saveImageToGallery(imageUrl: String) {
        coroutineScope.launch {
            try {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .build()

                val drawable = imageLoader.execute(request).drawable
                if (drawable is android.graphics.drawable.BitmapDrawable) {
                    val bitmap = drawable.bitmap

                    withContext(Dispatchers.IO) {
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, "Blossom_${System.currentTimeMillis()}.jpg")
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Blossom")
                        }

                        val uri = context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )

                        uri?.let { imageUri ->
                            context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Photo saved to gallery! 📸", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save photo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
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
            onAddImage(destUri.toString())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && cameraImageUriState.value != null) {
            // For now, just add the image - most modern camera apps handle orientation correctly
            onAddImage(cameraImageUriState.value.toString())
        }
    }

    fun launchCamera() {
        try {
            val photoFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "journal_${System.currentTimeMillis()}.jpg"
            )

            // Ensure the directory exists
            photoFile.parentFile?.mkdirs()

            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                photoFile
            )
            cameraImageUriState.value = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            // Handle camera launch errors gracefully
            e.printStackTrace()
        }
    }

    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            onNavigateBack()
            eventHandled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Entry" else "New Entry") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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
                onValueChange = { onTitleChanged(it) },
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
                onValueChange = { onContentChanged(it) },
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
                selectedMood = uiState.mood,
                onMoodSelected = { onMoodSelected(it) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Elegant Image Manager
            ElegantImageManager(
                imageUrls = uiState.imageUrls,
                featuredImageUrl = uiState.featuredImageUrl,
                onAddImage = { showImageSourceDialog = true },
                onDeleteImage = onDeleteImage,
                onSetFeaturedImage = onSetFeaturedImage,
                onSaveToGallery = { imageUrl ->
                    saveImageToGallery(imageUrl)
                },
                onImageClick = { imageUrls, index ->
                    fullScreenImageIndex = index
                    showFullScreenViewer = true
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button - like prayer dialog
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onNavigateBack) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { saveJournalEntry() },
                    enabled = uiState.title.isNotBlank()
                ) {
                    Text(if (isEditing) "Save Changes" else "Save Entry")
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
        }
    }

    // Image Source Selection Dialog
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = {
                Text(
                    "Add Photo",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column {
                    Text(
                        "Choose how you'd like to add a photo:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Gallery Option
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable {
                                galleryLauncher.launch("image/*")
                                showImageSourceDialog = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Gallery",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Choose from your photos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Camera Option
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable {
                                launchCamera()
                                showImageSourceDialog = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Camera",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Take a new photo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showImageSourceDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    // Full Screen Image Viewer
    if (showFullScreenViewer && uiState.imageUrls.isNotEmpty()) {
        FullScreenImageViewer(
            imageUrls = uiState.imageUrls,
            initialImageIndex = fullScreenImageIndex,
            onDismiss = { showFullScreenViewer = false }
        )
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