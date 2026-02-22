package dz.nadjahacademy.feature.blog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.blog.viewmodel.BlogDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogDetailScreen(
    slug: String,
    onBack: () -> Unit,
    onNavigateToBlog: (String) -> Unit,
    viewModel: BlogDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        uiState.post == null -> ErrorState(message = uiState.error ?: "Article not found", onRetry = {}, modifier = Modifier.fillMaxSize())
        else -> {
            val post = uiState.post!!
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } },
                        actions = {
                            IconButton(onClick = {}) { Icon(Icons.Filled.Share, "Share") }
                            IconButton(onClick = {}) { Icon(Icons.Filled.BookmarkBorder, "Bookmark") }
                        },
                    )
                },
            ) { padding ->
                Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
                    AsyncImage(
                        model = post.cover_url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                    )
                    Column(modifier = Modifier.padding(20.dp)) {
                        post.category?.let {
                            Text(it, style = MaterialTheme.typography.labelMedium, color = NadjahRed600, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))
                        }
                        Text(post.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            post.author_avatar?.let {
                                AsyncImage(model = it, contentDescription = null, modifier = Modifier.size(32.dp).clip(CircleShape))
                            }
                            Column {
                                Text(post.author_name ?: "", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                Text("${post.published_at ?: ""} · ${post.reading_time} min read", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        Text(post.content ?: "", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
