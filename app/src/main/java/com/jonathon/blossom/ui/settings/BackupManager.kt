package com.jonathon.blossom.ui.settings

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.GoogleAuthUtil
import java.io.File
import com.jonathon.blossom.data.BlossomDatabase
import com.jonathon.blossom.data.JournalEntry
import com.jonathon.blossom.data.PrayerRequest
import com.jonathon.blossom.data.MeditationSession
import com.jonathon.blossom.network.DriveApiService
import com.jonathon.blossom.network.FileMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: BlossomDatabase,
    private val moshi: Moshi,
    private val driveApiService: DriveApiService
) {

    private val TAG = "BackupManager"

    /**
     * Perform backup of app data to Google Drive
     */
    suspend fun performBackup(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account == null) {
                Log.e(TAG, "No Google account signed in")
                return@withContext Result.failure(Exception("No Google account signed in"))
            }

            // Get proper OAuth access token for Drive API
            val accessToken = try {
                GoogleAuthUtil.getToken(
                    context,
                    account.account!!,
                    "oauth2:https://www.googleapis.com/auth/drive.file"
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get access token: ${e.message}", e)
                return@withContext Result.failure(Exception("Failed to get access token: ${e.message}"))
            }
            val authHeader = "Bearer $accessToken"

            // Backup Journal Entries
            val journalEntries = database.journalDao().getAllEntriesSync()
            Log.i(TAG, "Found ${journalEntries.size} journal entries to backup")
            for (entry in journalEntries) {
                Log.d(TAG, "Entry ${entry.id}: title='${entry.title}', imageUrl='${entry.imageUrl}', featuredImageUrl='${entry.featuredImageUrl}', imageUrls='${entry.imageUrls}'")
            }
            val journalJson = moshi.adapter<List<JournalEntry>>(Types.newParameterizedType(List::class.java, JournalEntry::class.java)).toJson(journalEntries)
            Log.d(TAG, "Journal JSON size: ${journalJson.length} characters")
            uploadFileToDrive(authHeader, "journal_backup.json", journalJson)
            backupJournalPhotos(authHeader, journalEntries)

            // Backup Prayer Requests
            val prayerRequests = database.prayerRequestDao().getAllPrayerRequestsSync()
            val prayerJson = moshi.adapter<List<PrayerRequest>>(Types.newParameterizedType(List::class.java, PrayerRequest::class.java)).toJson(prayerRequests)
            uploadFileToDrive(authHeader, "prayer_backup.json", prayerJson)

            // Backup Meditation Sessions
            val meditationSessions = database.analyticsDao().getAllSessionsSync()
            val meditationJson = moshi.adapter<List<MeditationSession>>(Types.newParameterizedType(List::class.java, MeditationSession::class.java)).toJson(meditationSessions)
            uploadFileToDrive(authHeader, "meditation_backup.json", meditationJson)

            // Backup Habits
            val habits = database.dailyHabitDao().getAllHabits().first()
            val habitsJson = moshi.adapter<List<com.jonathon.blossom.data.DailyHabit>>(Types.newParameterizedType(List::class.java, com.jonathon.blossom.data.DailyHabit::class.java)).toJson(habits)
            uploadFileToDrive(authHeader, "habits_backup.json", habitsJson)

            // Backup Achievements
            val achievements = database.analyticsDao().getAllAchievements().first()
            val achievementsJson = moshi.adapter<List<com.jonathon.blossom.data.Achievement>>(Types.newParameterizedType(List::class.java, com.jonathon.blossom.data.Achievement::class.java)).toJson(achievements)
            uploadFileToDrive(authHeader, "achievements_backup.json", achievementsJson)

            // Backup User Settings
            val sharedPreferences = context.getSharedPreferences("blossom_settings", Context.MODE_PRIVATE)
            val settingsMap = mapOf(
                "selected_theme" to sharedPreferences.getString("selected_theme", null),
                "dark_mode" to sharedPreferences.getBoolean("dark_mode", false),
                "habit_reset_time" to sharedPreferences.getInt("habit_reset_time", 0)
            )
            val settingsJson = moshi.adapter<Map<String, Any?>>(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)).toJson(settingsMap)
            uploadFileToDrive(authHeader, "settings_backup.json", settingsJson)

            Log.i(TAG, "Backup completed successfully with REST API")
            return@withContext Result.success("Backup completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Backup failed: ${e.message}", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Restore data from Google Drive to local database
     */
    suspend fun performRestore(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account == null) {
                Log.e(TAG, "No Google account signed in")
                return@withContext Result.failure(Exception("No Google account signed in"))
            }

            // Get proper OAuth access token for Drive API
            val accessToken = try {
                GoogleAuthUtil.getToken(
                    context,
                    account.account!!,
                    "oauth2:https://www.googleapis.com/auth/drive.file"
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get access token: ${e.message}", e)
                return@withContext Result.failure(Exception("Failed to get access token: ${e.message}"))
            }
            val authHeader = "Bearer $accessToken"

            var totalRestored = 0
            val restoredItems = mutableListOf<String>()

            // Restore Journal Entries
            val journalJson = downloadFileFromDrive(authHeader, "journal_backup.json")
            if (journalJson != null) {
                Log.d(TAG, "Journal JSON content: ${journalJson.take(200)}...")
                val journalEntries = moshi.adapter<List<JournalEntry>>(Types.newParameterizedType(List::class.java, JournalEntry::class.java)).fromJson(journalJson)
                if (journalEntries != null && journalEntries.isNotEmpty()) {
                    database.journalDao().insertAll(journalEntries)
                    totalRestored += journalEntries.size
                    restoredItems.add("${journalEntries.size} journal entries")
                    Log.i(TAG, "Restored ${journalEntries.size} journal entries")

                    // Restore journal photos and update database paths
                    val photosRestored = restoreJournalPhotos(authHeader, journalEntries)
                    if (photosRestored > 0) {
                        restoredItems.add("$photosRestored photos")
                    }
                } else {
                    Log.w(TAG, "No journal entries found in backup or failed to parse")
                }
            } else {
                Log.w(TAG, "journal_backup.json not found on Drive")
            }

            // Restore Prayer Requests
            val prayerJson = downloadFileFromDrive(authHeader, "prayer_backup.json")
            if (prayerJson != null) {
                Log.d(TAG, "Prayer JSON content: ${prayerJson.take(200)}...")
                val prayerRequests = moshi.adapter<List<PrayerRequest>>(Types.newParameterizedType(List::class.java, PrayerRequest::class.java)).fromJson(prayerJson)
                if (prayerRequests != null && prayerRequests.isNotEmpty()) {
                    database.prayerRequestDao().insertAll(prayerRequests)
                    totalRestored += prayerRequests.size
                    restoredItems.add("${prayerRequests.size} prayer requests")
                    Log.i(TAG, "Restored ${prayerRequests.size} prayer requests")
                } else {
                    Log.w(TAG, "No prayer requests found in backup or failed to parse")
                }
            } else {
                Log.w(TAG, "prayer_backup.json not found on Drive")
            }

            // Restore Meditation Sessions
            val meditationJson = downloadFileFromDrive(authHeader, "meditation_backup.json")
            if (meditationJson != null) {
                Log.d(TAG, "Meditation JSON content: ${meditationJson.take(200)}...")
                val meditationSessions = moshi.adapter<List<MeditationSession>>(Types.newParameterizedType(List::class.java, MeditationSession::class.java)).fromJson(meditationJson)
                if (meditationSessions != null && meditationSessions.isNotEmpty()) {
                    database.analyticsDao().insertSessions(meditationSessions)
                    totalRestored += meditationSessions.size
                    restoredItems.add("${meditationSessions.size} meditation sessions")
                    Log.i(TAG, "Restored ${meditationSessions.size} meditation sessions")
                } else {
                    Log.w(TAG, "No meditation sessions found in backup or failed to parse")
                }
            } else {
                Log.w(TAG, "meditation_backup.json not found on Drive")
            }

            // Restore Habits
            val habitsJson = downloadFileFromDrive(authHeader, "habits_backup.json")
            if (habitsJson != null) {
                Log.d(TAG, "Habits JSON content: ${habitsJson.take(200)}...")
                val habits = moshi.adapter<List<com.jonathon.blossom.data.DailyHabit>>(Types.newParameterizedType(List::class.java, com.jonathon.blossom.data.DailyHabit::class.java)).fromJson(habitsJson)
                if (habits != null && habits.isNotEmpty()) {
                    for (habit in habits) {
                        database.dailyHabitDao().insert(habit)
                    }
                    totalRestored += habits.size
                    restoredItems.add("${habits.size} habits")
                    Log.i(TAG, "Restored ${habits.size} habits")
                } else {
                    Log.w(TAG, "No habits found in backup or failed to parse")
                }
            } else {
                Log.w(TAG, "habits_backup.json not found on Drive")
            }

            // Restore Achievements
            val achievementsJson = downloadFileFromDrive(authHeader, "achievements_backup.json")
            if (achievementsJson != null) {
                Log.d(TAG, "Achievements JSON content: ${achievementsJson.take(200)}...")
                val achievements = moshi.adapter<List<com.jonathon.blossom.data.Achievement>>(Types.newParameterizedType(List::class.java, com.jonathon.blossom.data.Achievement::class.java)).fromJson(achievementsJson)
                if (achievements != null && achievements.isNotEmpty()) {
                    for (achievement in achievements) {
                        database.analyticsDao().insertOrUpdateAchievement(achievement)
                    }
                    totalRestored += achievements.size
                    restoredItems.add("${achievements.size} achievements")
                    Log.i(TAG, "Restored ${achievements.size} achievements")
                } else {
                    Log.w(TAG, "No achievements found in backup or failed to parse")
                }
            } else {
                Log.w(TAG, "achievements_backup.json not found on Drive")
            }

            // Restore User Settings
            val settingsJson = downloadFileFromDrive(authHeader, "settings_backup.json")
            if (settingsJson != null) {
                Log.d(TAG, "Settings JSON content: ${settingsJson.take(200)}...")
                val settingsMap = moshi.adapter<Map<String, Any>>(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)).fromJson(settingsJson)
                if (settingsMap != null) {
                    val sharedPreferences = context.getSharedPreferences("blossom_settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    (settingsMap["selected_theme"] as? String)?.let { editor.putString("selected_theme", it) }
                    (settingsMap["dark_mode"] as? Boolean)?.let { editor.putBoolean("dark_mode", it) }
                    (settingsMap["habit_reset_time"] as? Double)?.let { editor.putInt("habit_reset_time", it.toInt()) } // Moshi may decode numbers as Double
                    editor.apply()
                    restoredItems.add("user settings")
                    Log.i(TAG, "Restored user settings: selected_theme='${settingsMap["selected_theme"]}', dark_mode='${settingsMap["dark_mode"]}', habit_reset_time='${settingsMap["habit_reset_time"]}'")
                } else {
                    Log.w(TAG, "Failed to parse settings data")
                }
            } else {
                Log.w(TAG, "settings_backup.json not found on Drive")
            }

            val resultMessage = if (totalRestored > 0) {
                "Restored: ${restoredItems.joinToString(", ")}"
            } else {
                "No backup files found or no data to restore"
            }

            Log.i(TAG, "Restore completed: $resultMessage")
            return@withContext Result.success(resultMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Restore failed: ${e.message}", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Upload a file to Google Drive
     */
    /*
    private suspend fun uploadFileToDrive(
        driveClient: com.google.android.gms.drive.DriveClient,
        folder: DriveFolder,
        fileName: String,
        content: String
    ) {
        // Check if file already exists
        val driveResourceClient = Drive.getDriveResourceClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
        val existingFile = findFileInFolder(driveResourceClient, folder, fileName)

        val driveContents: DriveContents
        if (existingFile != null) {
            // Open existing file for writing
            driveContents = Tasks.await(driveResourceClient.openFile(existingFile, DriveFile.MODE_WRITE_ONLY))
        } else {
            // Create new file
            driveContents = Tasks.await(driveClient.createContents())
        }

        // Write content to the file
        val outputStream = driveContents.outputStream
        OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
            writer.write(content)
        }

        // Commit the changes
        val changeSet = MetadataChangeSet.Builder()
            .setTitle(fileName)
            .setMimeType("application/json")
            .build()

        if (existingFile != null) {
            Tasks.await(driveResourceClient.commitContents(driveContents, changeSet))
            Log.i(TAG, "Updated existing file: $fileName")
        } else {
            val createResult = Tasks.await(driveClient.createFile(folder, changeSet, driveContents))
            Log.i(TAG, "Created new file: $fileName with ID: ${createResult.driveId}")
        }
    }
    */

    /**
     * Find a file in a folder by name
     */
    /*
    private suspend fun findFileInFolder(
        driveResourceClient: com.google.android.gms.drive.DriveResourceClient,
        folder: DriveFolder,
        fileName: String
    ): DriveFile? {
        val query = com.google.android.gms.drive.query.Query.Builder()
            .addFilter(com.google.android.gms.drive.query.Filters.eq(com.google.android.gms.drive.metadata.SearchableField.TITLE, fileName))
            .build()

        val queryResult = Tasks.await(driveResourceClient.queryChildren(folder, query))
        val metadataBuffer = queryResult.metadataBuffer

        return if (metadataBuffer.count > 0) {
            val metadata = metadataBuffer.get(0)
            metadata.driveId.asDriveFile()
        } else {
            null
        }
    }
    */

    /**
     * Download content of a file from Google Drive
     */
    /*
    private suspend fun downloadFileContent(
        driveResourceClient: com.google.android.gms.drive.DriveResourceClient,
        file: DriveFile
    ): String? {
        val driveContents = Tasks.await(driveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY))
        val inputStream = driveContents.inputStream
        val content = inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        Tasks.await(driveResourceClient.discardContents(driveContents))
        return content
    }
    */

    /**
     * Upload a file to Google Drive using REST API
     */
    private suspend fun uploadFileToDrive(authHeader: String, fileName: String, content: String) {
        try {
            // Prepare metadata for the file
            val metadata = FileMetadata(
                name = fileName,
                mimeType = "application/json"
            )
            val metadataJson = moshi.adapter(FileMetadata::class.java).toJson(metadata)
            val metadataPart = MultipartBody.Part.createFormData(
                "metadata",
                null,
                metadataJson.toRequestBody("application/json".toMediaTypeOrNull())
            )
            val mediaPart = MultipartBody.Part.createFormData(
                "file",
                fileName,
                content.toRequestBody("application/json".toMediaTypeOrNull())
            )

            // Upload file to Google Drive
            val response = driveApiService.uploadFile(authHeader, metadataPart, mediaPart)
            if (response.isSuccessful) {
                val fileResponse = response.body()
                Log.i(TAG, "Uploaded file: $fileName with ID: ${fileResponse?.id}")
            } else {
                Log.e(TAG, "Failed to upload file: $fileName, Error: ${response.errorBody()?.string()}")
                throw Exception("Failed to upload file: $fileName")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading file $fileName: ${e.message}", e)
            throw e
        }
    }

    /**
     * Download a file from Google Drive using REST API
     */
    private suspend fun downloadFileFromDrive(authHeader: String, fileName: String): String? {
        try {
            // Search for the file by name
            val query = "name='$fileName'"
            val listResponse = driveApiService.listFiles(authHeader, query)
            if (!listResponse.isSuccessful) {
                Log.e(TAG, "Failed to list files for $fileName: ${listResponse.errorBody()?.string()}")
                return null
            }

            val files = listResponse.body()?.files
            if (files.isNullOrEmpty()) {
                Log.w(TAG, "File $fileName not found on Google Drive")
                return null
            }

            val fileId = files[0].id
            val downloadResponse = driveApiService.getFile(authHeader, fileId)
            if (downloadResponse.isSuccessful) {
                val content = downloadResponse.body()?.string()
                Log.i(TAG, "Downloaded file: $fileName with ID: $fileId")
                return content
            } else {
                Log.e(TAG, "Failed to download file $fileName: ${downloadResponse.errorBody()?.string()}")
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading file $fileName: ${e.message}", e)
            return null
        }
    }

    /**
     * Backup journal photos to Google Drive
     */
    private suspend fun backupJournalPhotos(authHeader: String, journalEntries: List<JournalEntry>) {
        try {
            var totalPhotosFound = 0
            var totalPhotosBackedUp = 0

            for (entry in journalEntries) {
                Log.d(TAG, "Checking photos for entry ${entry.id}")

                // Backup images from imageUrls field (pipe-separated)
                if (entry.imageUrls.isNotEmpty()) {
                    val imageUrls = entry.imageUrls.split("|").filter { it.isNotEmpty() }
                    Log.d(TAG, "Entry ${entry.id} has ${imageUrls.size} images in imageUrls: $imageUrls")
                    totalPhotosFound += imageUrls.size
                    for ((index, imageUrl) in imageUrls.withIndex()) {
                        val success = backupSinglePhoto(authHeader, imageUrl, "journal_${entry.id}_${index}")
                        if (success) totalPhotosBackedUp++
                    }
                }

                // Backup featured image
                entry.featuredImageUrl?.let { featuredUrl ->
                    Log.d(TAG, "Entry ${entry.id} has featured image: $featuredUrl")
                    totalPhotosFound++
                    val success = backupSinglePhoto(authHeader, featuredUrl, "journal_${entry.id}_featured")
                    if (success) totalPhotosBackedUp++
                }

                // Backup legacy single image
                entry.imageUrl?.let { legacyUrl ->
                    Log.d(TAG, "Entry ${entry.id} has legacy image: $legacyUrl")
                    totalPhotosFound++
                    val success = backupSinglePhoto(authHeader, legacyUrl, "journal_${entry.id}_legacy")
                    if (success) totalPhotosBackedUp++
                }
            }
            Log.i(TAG, "Journal photos backup completed: $totalPhotosBackedUp/$totalPhotosFound photos backed up successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error backing up journal photos: ${e.message}", e)
            // Don't fail the entire backup if photos fail
        }
    }

    /**
     * Backup a single photo to Google Drive
     */
    private suspend fun backupSinglePhoto(authHeader: String, imageUrl: String, fileName: String): Boolean {
        try {
            Log.d(TAG, "Attempting to backup photo: $imageUrl -> $fileName")
            val uri = Uri.parse(imageUrl)

            // Handle content:// URIs (FileProvider URIs) properly
            val imageBytes = if (uri.scheme == "content") {
                Log.d(TAG, "Reading content URI: $imageUrl")
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.readBytes()
                    } ?: run {
                        Log.w(TAG, "Failed to open input stream for: $imageUrl")
                        return false
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error reading content URI $imageUrl: ${e.message}")
                    return false
                }
            } else {
                // Handle file:// URIs (direct file paths)
                Log.d(TAG, "Reading file URI: $imageUrl")
                val file = File(uri.path ?: run {
                    Log.w(TAG, "Invalid URI path for: $imageUrl")
                    return false
                })

                if (!file.exists()) {
                    Log.w(TAG, "Photo file not found: ${file.path}")
                    return false
                }

                file.readBytes()
            }

            Log.d(TAG, "Photo read successfully, size: ${imageBytes.size} bytes")

            // Encode to Base64
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            Log.d(TAG, "Encoded photo to Base64, size: ${base64Image.length} characters")

            // Upload as JSON with Base64 content
            val photoData = mapOf(
                "fileName" to fileName,
                "originalPath" to imageUrl,
                "imageData" to base64Image,
                "mimeType" to "image/jpeg"
            )
            val photoJson = moshi.adapter<Map<String, String>>(Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)).toJson(photoData)

            uploadFileToDrive(authHeader, "${fileName}.json", photoJson)
            Log.i(TAG, "Successfully backed up photo: $fileName")
            return true

        } catch (e: Exception) {
            Log.e(TAG, "Error backing up photo $fileName: ${e.message}", e)
            return false
        }
    }

    /**
     * Restore journal photos from Google Drive and update database paths
     */
    private suspend fun restoreJournalPhotos(authHeader: String, journalEntries: List<JournalEntry>): Int {
        var photosRestored = 0
        try {
            for (entry in journalEntries) {
                var updatedEntry = entry
                var needsUpdate = false
                val restoredImagePaths = mutableListOf<String>()

                // Restore images from imageUrls field (pipe-separated)
                if (entry.imageUrls.isNotEmpty()) {
                    val imageUrls = entry.imageUrls.split("|").filter { it.isNotEmpty() }
                    Log.d(TAG, "Restoring ${imageUrls.size} images from imageUrls for entry ${entry.id}")

                    for ((index, imageUrl) in imageUrls.withIndex()) {
                        val photoJson = downloadFileFromDrive(authHeader, "journal_${entry.id}_${index}.json")
                        if (photoJson != null) {
                            val newPath = restoreSinglePhoto(photoJson, "journal_${entry.id}_${index}")
                            if (newPath != null) {
                                restoredImagePaths.add(newPath)
                                photosRestored++
                                Log.d(TAG, "Restored imageUrls[$index] for entry ${entry.id}: $newPath")
                            }
                        }
                    }

                    // Update imageUrls field with restored paths
                    if (restoredImagePaths.isNotEmpty()) {
                        updatedEntry = updatedEntry.copy(imageUrls = restoredImagePaths.joinToString("|"))
                        needsUpdate = true
                        Log.d(TAG, "Updated imageUrls for entry ${entry.id}: ${updatedEntry.imageUrls}")
                    }
                }

                // Restore featured image
                if (!entry.featuredImageUrl.isNullOrEmpty()) {
                    val photoJson = downloadFileFromDrive(authHeader, "journal_${entry.id}_featured.json")
                    if (photoJson != null) {
                        val newPath = restoreSinglePhoto(photoJson, "journal_${entry.id}_featured")
                        if (newPath != null) {
                            updatedEntry = updatedEntry.copy(featuredImageUrl = newPath)
                            needsUpdate = true
                            photosRestored++
                            Log.d(TAG, "Restored featured image for entry ${entry.id}: $newPath")
                        }
                    }
                }

                // Restore legacy single image
                if (!entry.imageUrl.isNullOrEmpty()) {
                    val photoJson = downloadFileFromDrive(authHeader, "journal_${entry.id}_legacy.json")
                    if (photoJson != null) {
                        val newPath = restoreSinglePhoto(photoJson, "journal_${entry.id}_legacy")
                        if (newPath != null) {
                            updatedEntry = updatedEntry.copy(imageUrl = newPath)
                            needsUpdate = true
                            photosRestored++
                            Log.d(TAG, "Restored legacy image for entry ${entry.id}: $newPath")
                        }
                    }
                }

                // Update database if any photos were restored
                if (needsUpdate) {
                    database.journalDao().insertJournalEntry(updatedEntry)
                    Log.d(TAG, "Updated database paths for journal entry ${entry.id}")
                    Log.d(TAG, "Final entry paths - imageUrls: '${updatedEntry.imageUrls}', featuredImageUrl: '${updatedEntry.featuredImageUrl}', imageUrl: '${updatedEntry.imageUrl}'")
                }
            }
            Log.i(TAG, "Restored $photosRestored journal photos")
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring journal photos: ${e.message}", e)
        }
        return photosRestored
    }

    /**
     * Restore a single photo from JSON data and return the new file path
     */
    private suspend fun restoreSinglePhoto(photoJson: String, fileName: String): String? {
        try {
            val photoData = moshi.adapter<Map<String, String>>(Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)).fromJson(photoJson)
            if (photoData == null) {
                Log.w(TAG, "Failed to parse photo data for $fileName")
                return null
            }

            val base64Image = photoData["imageData"] ?: return null
            val originalPath = photoData["originalPath"] ?: return null
            val originalFileName = photoData["fileName"] ?: "restored_photo.jpg"

            // Decode Base64 to bytes
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)

            // Create new file in app's pictures directory with a single 'restored_' prefix and timestamp
            val restoredFile = File(
                context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
                "restored_${System.currentTimeMillis()}_${originalFileName}"
            )

            // Ensure directory exists
            restoredFile.parentFile?.mkdirs()

            // Write the image data
            restoredFile.writeBytes(imageBytes)

            // Return the file URI that the app can use
            val fileUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                restoredFile
            )

            Log.d(TAG, "Restored photo: ${restoredFile.path} -> URI: $fileUri")
            return fileUri.toString()

        } catch (e: Exception) {
            Log.e(TAG, "Error restoring photo $fileName: ${e.message}", e)
            return null
        }
    }
}
