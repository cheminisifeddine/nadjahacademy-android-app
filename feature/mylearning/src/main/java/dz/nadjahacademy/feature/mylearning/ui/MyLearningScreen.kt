package dz.nadjahacademy.feature.mylearning.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dz.nadjahacademy.core.network.model.*
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.mylearning.viewmodel.MyLearningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLearningScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToLesson: (String, String) -> Unit,
    onNavigateToCertificate: (String) -> Unit,
    onNavigateToCheckout: (String) -> Unit,
    viewModel: MyLearningViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("In Progress", "Completed")

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Top bar
        Surface(shadowElevation = 2.dp) {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("My Learning", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }
                TabRow(selectedTabIndex = uiState.selectedTab) {
                    tabs.forEachIndexed { i, tab ->
                        Tab(selected = uiState.selectedTab == i, onClick = { viewModel.selectTab(i) }, text = { Text(tab) })
                    }
                }
            }
        }

        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            else -> {
                val items = if (uiState.selectedTab == 0) uiState.enrollments else uiState.completedCourses
                if (items.isEmpty()) {
                    EmptyState(
                        icon = Icons.Outlined.School,
                        title = if (uiState.selectedTab == 0) "No courses in progress" else "No completed courses",
                        message = if (uiState.selectedTab == 0) "Enroll in a course to get started" else "Complete a course to see it here",
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(items) { enrollment ->
                            EnrollmentCard(enrollment = enrollment, onClick = {
                                onNavigateToCourse(enrollment.course_id)
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnrollmentCard(enrollment: EnrolledCourse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = enrollment.thumbnail_url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(enrollment.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(enrollment.instructor_name ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                val progress = enrollment.progress_percentage.toInt()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = if (progress >= 100) NadjahGreen500 else NadjahRed600,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("$progress%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    if (progress >= 100) "Completed" else "Continue Learning",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (progress >= 100) NadjahGreen600 else NadjahRed600,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
