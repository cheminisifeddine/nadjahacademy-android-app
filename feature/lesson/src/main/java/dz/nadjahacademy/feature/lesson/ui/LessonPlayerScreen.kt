package dz.nadjahacademy.feature.lesson.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import dz.nadjahacademy.core.network.model.*
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.lesson.viewmodel.LessonViewModel
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun LessonPlayerScreen(
    lessonId: String,
    courseId: String,
    onBack: () -> Unit,
    onNavigateToQuiz: (String) -> Unit,
    onNavigateToDiscussion: (String) -> Unit = {},
    onNextLesson: (String) -> Unit = {},
    viewModel: LessonViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isFullscreen by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // ExoPlayer setup
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveProgress(exoPlayer.currentPosition)
            exoPlayer.release()
        }
    }

    // Set media when lesson loads
    LaunchedEffect(uiState.lesson?.video_url) {
        uiState.lesson?.video_url?.let { url ->
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
            if (uiState.currentPosition > 0) {
                exoPlayer.seekTo(uiState.currentPosition)
            }
            exoPlayer.playWhenReady = true
        }
    }

    // Auto-save progress every 15 seconds
    LaunchedEffect(exoPlayer.isPlaying) {
        while (true) {
            delay(15_000)
            if (exoPlayer.isPlaying) {
                viewModel.saveProgress(exoPlayer.currentPosition)
            }
        }
    }

    // Auto-mark complete at 90% watched
    LaunchedEffect(Unit) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
                val duration = exoPlayer.duration
                val position = exoPlayer.currentPosition
                if (duration > 0 && position.toFloat() / duration >= 0.9f) {
                    viewModel.markComplete()
                }
            }
        })
    }

    BackHandler(enabled = isFullscreen) {
        isFullscreen = false
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    if (isFullscreen) {
        // Fullscreen video
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .systemBarsPadding(),
        ) {
            VideoPlayerView(
                player = exoPlayer,
                modifier = Modifier.fillMaxSize(),
            )
            IconButton(
                onClick = {
                    isFullscreen = false
                    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                },
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
            ) {
                Icon(Icons.Filled.FullscreenExit, contentDescription = "Exit fullscreen", tint = NadjahWhite)
            }
        }
        return
    }

    when {
        uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        uiState.lesson == null -> ErrorState(message = uiState.error ?: "Lesson Not Found", onRetry = viewModel::loadLesson, modifier = Modifier.fillMaxSize())
        else -> {
            val lesson = uiState.lesson!!
            Column(modifier = Modifier.fillMaxSize()) {
                // Video player
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black),
                ) {
                    VideoPlayerView(player = exoPlayer, modifier = Modifier.fillMaxSize())
                    // Top bar overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = { viewModel.saveProgress(exoPlayer.currentPosition); onBack() },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = NadjahWhite)
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                isFullscreen = true
                                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                        ) {
                            Icon(Icons.Filled.Fullscreen, contentDescription = "Fullscreen", tint = NadjahWhite)
                        }
                    }
                }

                // Lesson info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            lesson.section_title ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (uiState.isCompleted) {
                        Surface(color = NadjahGreen100, shape = RoundedCornerShape(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = NadjahGreen600, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Completed", style = MaterialTheme.typography.labelSmall, color = NadjahGreen600)
                            }
                        }
                    } else {
                        NadjahOutlinedButton(
                            text = "Mark Complete",
                            onClick = viewModel::markComplete,
                            modifier = Modifier.height(36.dp),
                        )
                    }
                }

                // Navigation arrows
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    lesson.prev_lesson?.let { prev ->
                        NadjahOutlinedButton(
                            text = "← Previous",
                            onClick = { onNextLesson(prev.id) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    lesson.next_lesson?.let { next ->
                        NadjahButton(
                            text = if (next.type == "quiz") "Take Quiz →" else "Next →",
                            onClick = {
                                if (next.type == "quiz") onNavigateToQuiz(next.id)
                                else onNextLesson(next.id)
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                HorizontalDivider()

                // Tabs: Overview, Notes, Resources
                val tabs = listOf("Overview", "Notes", "Resources")
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> lessonOverviewItems(lesson)
                        1 -> lessonNotesItems(uiState.notes, onAddNote = viewModel::showNoteDialog, onDeleteNote = viewModel::deleteNote)
                        2 -> lessonResourcesItems(lesson.resources ?: emptyList())
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }

            // Add Note dialog
            if (uiState.showNoteDialog) {
                AddNoteDialog(
                    currentTimestamp = (exoPlayer.currentPosition / 1000).toInt(),
                    onAdd = viewModel::addNote,
                    onDismiss = viewModel::hideNoteDialog,
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayerView(player: ExoPlayer, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
            }
        },
        modifier = modifier,
    )
}

private fun LazyListScope.lessonOverviewItems(lesson: LessonDetail) {
    if (!lesson.description.isNullOrBlank()) {
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("About This Lesson", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text(lesson.description ?: "", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private fun LazyListScope.lessonNotesItems(notes: List<Note>, onAddNote: () -> Unit, onDeleteNote: (String) -> Unit) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("My Notes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            NadjahTextButton(text = "+ Add Note", onClick = onAddNote)
        }
    }
    if (notes.isEmpty()) {
        item {
            EmptyState(
                icon = Icons.Outlined.NoteAdd,
                title = "No Notes Yet",
                message = "Add notes while watching to remember key points",
                modifier = Modifier.padding(32.dp),
            )
        }
    } else {
        items(notes, key = { it.id }) { note ->
            NoteItem(note = note, onDelete = { onDeleteNote(note.id) })
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

private fun LazyListScope.lessonResourcesItems(resources: List<LessonResource>) {
    if (resources.isEmpty()) {
        item {
            EmptyState(
                icon = Icons.Outlined.Attachment,
                title = "No Resources",
                message = "This lesson has no downloadable resources",
                modifier = Modifier.padding(32.dp),
            )
        }
    } else {
        item {
            Text("Downloadable Resources", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(16.dp))
        }
        items(resources) { resource ->
            ListItem(
                leadingContent = { Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = NadjahRed600) },
                headlineContent = { Text(resource.title) },
                supportingContent = resource.file_size?.let { { Text(it.toString()) } },
                trailingContent = {
                    IconButton(onClick = { /* download */ }) {
                        Icon(Icons.Outlined.Download, contentDescription = "Download")
                    }
                },
                modifier = Modifier.clickable { },
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun NoteItem(note: Note, onDelete: () -> Unit) {
    ListItem(
        leadingContent = {
            if (note.timestamp != null) {
                Surface(color = NadjahRed100, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        formatTimestamp(note.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = NadjahCharcoal600,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    )
                }
            }
        },
        headlineContent = { Text(note.content, style = MaterialTheme.typography.bodyMedium) },
        supportingContent = { Text(note.created_at.take(10), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete note", tint = MaterialTheme.colorScheme.error)
            }
        },
    )
}

@Composable
private fun AddNoteDialog(currentTimestamp: Int, onAdd: (String, Int?) -> Unit, onDismiss: () -> Unit) {
    var content by remember { mutableStateOf("") }
    var addTimestamp by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Note") },
        text = {
            Column {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = addTimestamp, onCheckedChange = { addTimestamp = it })
                    Spacer(Modifier.width(4.dp))
                    Text("Add timestamp: ${formatTimestamp(currentTimestamp)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (content.isNotBlank()) onAdd(content, if (addTimestamp) currentTimestamp else null) },
                enabled = content.isNotBlank(),
            ) {
                Text("Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

private fun formatTimestamp(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m}:${s.toString().padStart(2, '0')}"
}
