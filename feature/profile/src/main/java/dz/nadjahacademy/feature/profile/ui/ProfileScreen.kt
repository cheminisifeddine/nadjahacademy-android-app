package dz.nadjahacademy.feature.profile.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.profile.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToCertificate: (String) -> Unit = {},
    onNavigateToHelpSupport: () -> Unit = {},
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggingOut) {
        if (uiState.isLoggingOut) onLogout()
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; viewModel.logout() }) {
                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } },
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()),
    ) {
        // Header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.verticalGradient(listOf(NadjahCharcoal600, NadjahGold600))),
        ) {
            IconButton(onClick = onNavigateToSettings, modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(8.dp)) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = NadjahWhite)
            }
        }

        // Avatar overlapping header
        Box(modifier = Modifier.fillMaxWidth().offset(y = (-50).dp), contentAlignment = Alignment.Center) {
            Box {
                AsyncImage(
                    model = uiState.user?.avatar_url,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp).clip(CircleShape).border(4.dp, MaterialTheme.colorScheme.surface, CircleShape),
                )
                IconButton(
                    onClick = onNavigateToEditProfile,
                    modifier = Modifier.align(Alignment.BottomEnd).size(32.dp).clip(CircleShape).background(NadjahRed600),
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = NadjahWhite, modifier = Modifier.size(16.dp))
                }
            }
        }

        // User info
        Column(
            modifier = Modifier.fillMaxWidth().offset(y = (-40).dp).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                uiState.user?.full_name ?: "Loading...",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            uiState.user?.headline?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(16.dp))

            // Stats row
            uiState.stats?.let { stats ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        ProfileStat("${stats.enrolled_courses}", "Courses")
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        ProfileStat("${stats.completed_courses}", "Completed")
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        ProfileStat("${stats.total_certificates}", "Certificates")
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        ProfileStat("${stats.learning_streak}d", "Streak")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Menu items
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column {
                    ProfileMenuItem(icon = Icons.Outlined.EmojiEvents, title = "Achievements & Certificates", onClick = onNavigateToAchievements)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(icon = Icons.Outlined.Settings, title = "Settings", onClick = onNavigateToSettings)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(icon = Icons.Outlined.Help, title = "Help & Support", onClick = onNavigateToHelpSupport)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(icon = Icons.Outlined.Info, title = "About", onClick = {})
                }
            }

            Spacer(Modifier.height(16.dp))

            NadjahOutlinedButton(
                text = "Sign Out",
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = NadjahRed600, modifier = Modifier.size(22.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}
