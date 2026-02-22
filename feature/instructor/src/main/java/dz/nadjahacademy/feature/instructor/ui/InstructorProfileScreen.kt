package dz.nadjahacademy.feature.instructor.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dz.nadjahacademy.core.network.model.CourseListItem
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.instructor.viewmodel.InstructorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorProfileScreen(
    slug: String,
    onBack: () -> Unit,
    onNavigateToCourse: (String) -> Unit,
    viewModel: InstructorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        uiState.instructor == null -> ErrorState(message = uiState.error ?: "Not Found", onRetry = {}, modifier = Modifier.fillMaxSize())
        else -> {
            val instructor = uiState.instructor!!
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                // Header
                Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(NadjahCharcoal600)) {
                    IconButton(onClick = onBack, modifier = Modifier.statusBarsPadding().padding(8.dp)) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = NadjahWhite)
                    }
                }

                // Avatar
                Box(modifier = Modifier.fillMaxWidth().offset(y = (-50).dp)) {
                    Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.Bottom) {
                        AsyncImage(
                            model = instructor.avatar_url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(90.dp).clip(CircleShape).border(4.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(instructor.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            instructor.headline?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }

                Column(modifier = Modifier.offset(y = (-30).dp).padding(horizontal = 20.dp)) {
                    // Stats
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            InstructorStat("${instructor.average_rating}", "Rating")
                            VerticalDivider(modifier = Modifier.height(36.dp))
                            InstructorStat("${instructor.total_students}", "Students")
                            VerticalDivider(modifier = Modifier.height(36.dp))
                            InstructorStat("${instructor.total_courses}", "Courses")
                        }
                    }
                    Spacer(Modifier.height(20.dp))

                    // Bio
                    instructor.bio?.let {
                        Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(20.dp))
                    }

                    // Courses
                    if (uiState.courses.isNotEmpty()) {
                        Text("Courses by ${instructor.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(uiState.courses) { course ->
                                InstructorCourseCard(course = course, onClick = { onNavigateToCourse(course.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructorStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun InstructorCourseCard(course: CourseListItem, onClick: () -> Unit) {
    Card(modifier = Modifier.width(180.dp).clickable(onClick = onClick), shape = RoundedCornerShape(10.dp)) {
        Column {
            AsyncImage(
                model = course.thumbnail_url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(100.dp),
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(course.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = StarYellow, modifier = Modifier.size(12.dp))
                    Text(" ${course.average_rating}", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.weight(1f))
                    Text(if (course.price == 0.0 || course.price == null) "Free" else "${course.price} DZD", style = MaterialTheme.typography.labelSmall, color = NadjahCharcoal600, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
