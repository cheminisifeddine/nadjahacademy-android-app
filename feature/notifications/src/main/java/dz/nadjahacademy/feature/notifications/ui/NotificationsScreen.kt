package dz.nadjahacademy.feature.notifications.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dz.nadjahacademy.core.network.model.Notification
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.notifications.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToBlog: (String) -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(shadowElevation = 2.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                Text("Notifications", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                TextButton(onClick = viewModel::markAllAsRead) { Text("Mark all read") }
            }
        }

        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            uiState.notifications.isEmpty() -> EmptyState(
                icon = Icons.Outlined.Notifications,
                title = "No notifications",
                message = "You're all caught up!",
                modifier = Modifier.fillMaxSize(),
            )
            else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { if (!notification.is_read) viewModel.markAsRead(notification.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.is_read) MaterialTheme.colorScheme.surface else NadjahRed50,
        ),
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(NadjahRed100),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    notificationIcon(notification.type),
                    contentDescription = null,
                    tint = NadjahRed600,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.title, style = MaterialTheme.typography.bodyMedium, fontWeight = if (notification.is_read) FontWeight.Normal else FontWeight.SemiBold)
                Text(notification.body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(notification.created_at ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!notification.is_read) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NadjahRed600))
            }
        }
    }
}

private fun notificationIcon(type: String?): ImageVector = when (type) {
    "course_update" -> Icons.Outlined.School
    "new_lesson" -> Icons.Outlined.PlayCircleOutline
    "quiz_result" -> Icons.Outlined.Quiz
    "certificate" -> Icons.Outlined.EmojiEvents
    "payment" -> Icons.Outlined.Payment
    else -> Icons.Outlined.Notifications
}
