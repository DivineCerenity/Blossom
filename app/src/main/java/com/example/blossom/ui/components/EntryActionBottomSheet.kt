package com.example.blossom.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 📱 ENTRY ACTION BOTTOM SHEET
 * Reusable bottom sheet for Journal and Prayer entry actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryActionBottomSheet(
    isVisible: Boolean,
    title: String,
    actions: List<BottomSheetAction>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = modifier,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            dragHandle = {
                Surface(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(width = 32.dp, height = 4.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                ) {}
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Action buttons
                actions.forEach { action ->
                    ActionButton(
                        action = action,
                        onClick = {
                            action.onClick()
                            onDismiss()
                        }
                    )
                }
                
                // Bottom spacing for safe area
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * 🔘 ACTION BUTTON
 */
@Composable
private fun ActionButton(
    action: BottomSheetAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),  // 🌸 EXPLICIT ROUNDED CORNERS
        color = when (action.type) {
            ActionType.DESTRUCTIVE -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            ActionType.PRIMARY -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ActionType.SECONDARY -> MaterialTheme.colorScheme.surfaceVariant
        },
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = when (action.type) {
                    ActionType.DESTRUCTIVE -> MaterialTheme.colorScheme.error
                    ActionType.PRIMARY -> MaterialTheme.colorScheme.primary
                    ActionType.SECONDARY -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(24.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = when (action.type) {
                        ActionType.DESTRUCTIVE -> MaterialTheme.colorScheme.error
                        ActionType.PRIMARY -> MaterialTheme.colorScheme.primary
                        ActionType.SECONDARY -> MaterialTheme.colorScheme.onSurface
                    }
                )
                
                if (action.subtitle != null) {
                    Text(
                        text = action.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 📋 BOTTOM SHEET ACTION DATA CLASS
 */
data class BottomSheetAction(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val type: ActionType = ActionType.SECONDARY,
    val onClick: () -> Unit
)

/**
 * 🎨 ACTION TYPES
 */
enum class ActionType {
    PRIMARY,    // Blue/Primary color
    SECONDARY,  // Default/Gray color  
    DESTRUCTIVE // Red/Error color
}

/**
 * 📝 JOURNAL ENTRY ACTIONS
 */
object JournalActions {
    fun getActions(
        onEdit: () -> Unit,
        onDelete: () -> Unit
    ): List<BottomSheetAction> = listOf(
        BottomSheetAction(
            title = "Edit Entry",
            subtitle = "Modify this journal entry",
            icon = Icons.Default.Edit,
            type = ActionType.PRIMARY,
            onClick = onEdit
        ),
        BottomSheetAction(
            title = "Delete Entry",
            subtitle = "Permanently remove this entry",
            icon = Icons.Default.Delete,
            type = ActionType.DESTRUCTIVE,
            onClick = onDelete
        )
    )
}

/**
 * 🙏 PRAYER REQUEST ACTIONS
 */
object PrayerActions {
    fun getActions(
        isAnswered: Boolean,
        onEdit: () -> Unit,
        onDelete: () -> Unit,
        onToggleAnswered: () -> Unit
    ): List<BottomSheetAction> = listOf(
        BottomSheetAction(
            title = "Edit Prayer",
            subtitle = "Modify this prayer request",
            icon = Icons.Default.Edit,
            type = ActionType.PRIMARY,
            onClick = onEdit
        ),
        BottomSheetAction(
            title = if (isAnswered) "Mark as Unanswered" else "Mark as Answered",
            subtitle = if (isAnswered) "Move back to active prayers" else "Celebrate this answered prayer",
            icon = Icons.Default.Check,
            type = ActionType.PRIMARY,
            onClick = onToggleAnswered
        ),
        BottomSheetAction(
            title = "Delete Prayer",
            subtitle = "Permanently remove this prayer",
            icon = Icons.Default.Delete,
            type = ActionType.DESTRUCTIVE,
            onClick = onDelete
        )
    )
}
