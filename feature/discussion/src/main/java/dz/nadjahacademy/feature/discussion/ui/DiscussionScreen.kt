package dz.nadjahacademy.feature.discussion.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dz.nadjahacademy.core.network.model.Discussion
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.discussion.viewmodel.DiscussionViewModel

@Composable
fun DiscussionScreen(
    courseId: String,
    lessonId: String?,
    onBack: () -> Unit,
    viewModel: DiscussionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(shadowElevation = 2.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                Text("Discussion", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }

        when {
            uiState.isLoading -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            uiState.posts.isEmpty() -> EmptyState(
                icon = Icons.Outlined.Forum,
                title = "No discussions yet",
                message = "Be the first to start a conversation",
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
            else -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.posts) { post ->
                    DiscussionPostItem(
                        post = post,
                        onLike = { viewModel.likePost(post.id) },
                        onReply = { viewModel.setReplyText("@${post.user_name ?: ""} ") },
                    )
                }
            }
        }

        // Reply input
        Surface(shadowElevation = 8.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = uiState.replyText,
                    onValueChange = viewModel::setReplyText,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask a question or share thoughts...") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                )
                IconButton(
                    onClick = { viewModel.postMessage() },
                    enabled = uiState.replyText.isNotBlank() && !uiState.isPosting,
                    modifier = Modifier.clip(CircleShape).background(NadjahRed600),
                ) {
                    if (uiState.isPosting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NadjahWhite)
                    else Icon(Icons.Filled.Send, "Send", tint = NadjahWhite)
                }
            }
        }
    }
}

@Composable
private fun DiscussionPostItem(post: Discussion, onLike: () -> Unit, onReply: () -> Unit) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AsyncImage(
                model = post.user_avatar,
                contentDescription = null,
                modifier = Modifier.size(36.dp).clip(CircleShape),
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(post.user_name ?: "Student", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    if (post.user_role == "instructor") {
                        Surface(color = NadjahRed600, shape = RoundedCornerShape(4.dp)) {
                            Text("Instructor", style = MaterialTheme.typography.labelSmall, color = NadjahWhite, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Text(post.created_at, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                Text(post.body, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.clickable(onClick = onLike),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Outlined.ThumbUp, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${post.upvotes}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(
                        modifier = Modifier.clickable(onClick = onReply),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Outlined.Reply, null, modifier = Modifier.size(16.dp), tint = NadjahRed600)
                        Text("Reply", style = MaterialTheme.typography.labelSmall, color = NadjahRed600)
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
    }
}
