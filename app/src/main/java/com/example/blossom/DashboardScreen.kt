package com.example.blossom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.blossom.ui.dashboard.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // We can give this some top padding to space it from the TopAppBar
                    .padding(top = 16.dp)
                    .defaultMinSize(minHeight = 150.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                    }
                    uiState.error != null -> {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        DailyVerseCard(
                            verseText = uiState.verseText,
                            verseReference = uiState.verseReference
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyVerseCard(verseText: String, verseReference: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Verse of the Day",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"$verseText\"",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = verseReference,
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DashboardScreenPreview() {
    DailyVerseCard(
        verseText = "For I know the plans I have for you,\" declares the Lord...",
        verseReference = "Jeremiah 29:11"
    )
}