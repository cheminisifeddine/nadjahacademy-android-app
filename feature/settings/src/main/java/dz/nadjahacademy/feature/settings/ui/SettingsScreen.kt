package dz.nadjahacademy.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToWebView: (String) -> Unit,
    onNavigateToHelpSupport: () -> Unit,
    onLogout: () -> Unit,
    onAbout: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showQualityDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        val languages = listOf("en" to "English", "ar" to "Arabic", "fr" to "French")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Language") },
            text = {
                Column {
                    languages.forEach { (code, name) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.setLanguage(code); showLanguageDialog = false }.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = uiState.language == code, onClick = { viewModel.setLanguage(code); showLanguageDialog = false })
                            Text(name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showLanguageDialog = false }) { Text("Cancel") } },
        )
    }

    if (showQualityDialog) {
        val qualities = listOf("auto" to "Auto", "1080p" to "1080p HD", "720p" to "720p", "480p" to "480p", "360p" to "360p")
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text("Video Quality") },
            text = {
                Column {
                    qualities.forEach { (q, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.setVideoQuality(q); showQualityDialog = false }.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = uiState.videoQuality == q, onClick = { viewModel.setVideoQuality(q); showQualityDialog = false })
                            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showQualityDialog = false }) { Text("Cancel") } },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            SettingsSection("Appearance") {
                SettingsToggleItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Theme",
                    subtitle = "Switch to dark color scheme",
                    checked = uiState.isDarkTheme,
                    onToggle = viewModel::toggleDarkTheme,
                )
                SettingsClickItem(
                    icon = Icons.Outlined.Language,
                    title = "Language",
                    subtitle = when (uiState.language) { "ar" -> "Arabic"; "fr" -> "French"; else -> "English" },
                    onClick = { showLanguageDialog = true },
                )
            }

            SettingsSection("Notifications") {
                SettingsToggleItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Push Notifications",
                    subtitle = "Receive course updates and reminders",
                    checked = uiState.notificationsEnabled,
                    onToggle = viewModel::toggleNotifications,
                )
            }

            SettingsSection("Downloads") {
                SettingsToggleItem(
                    icon = Icons.Outlined.Wifi,
                    title = "Download over Wi-Fi only",
                    subtitle = "Save mobile data",
                    checked = uiState.downloadOverWifiOnly,
                    onToggle = viewModel::toggleWifiOnly,
                )
            }

            SettingsSection("Playback") {
                SettingsToggleItem(
                    icon = Icons.Outlined.PlayArrow,
                    title = "Auto-play next lesson",
                    subtitle = "Automatically continue to the next lesson",
                    checked = uiState.autoPlayNext,
                    onToggle = viewModel::toggleAutoPlay,
                )
                SettingsClickItem(
                    icon = Icons.Outlined.VideoSettings,
                    title = "Video Quality",
                    subtitle = uiState.videoQuality.replaceFirstChar { it.uppercase() },
                    onClick = { showQualityDialog = true },
                )
            }

            SettingsSection("Support") {
                SettingsClickItem(icon = Icons.Outlined.Help, title = "Help & Support", subtitle = "FAQs and contact us", onClick = onNavigateToHelpSupport)
                SettingsClickItem(icon = Icons.Outlined.Info, title = "About", subtitle = "App version and legal info", onClick = onAbout)
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, style = MaterialTheme.typography.labelMedium, color = NadjahRed600, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp))
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
        Column(content = content)
    }
}

@Composable
private fun SettingsToggleItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = NadjahRed600, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}

@Composable
private fun SettingsClickItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = NadjahRed600, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}
