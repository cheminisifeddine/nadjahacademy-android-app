package dz.nadjahacademy.feature.course.ui

import androidx.compose.foundation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dz.nadjahacademy.core.network.model.*
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.course.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    slug: String,
    onBack: () -> Unit,
    onNavigateToLesson: (String, String) -> Unit,
    onNavigateToInstructor: (String) -> Unit,
    onNavigateToCheckout: (String) -> Unit,
    onNavigateToDiscussion: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onStartLearning: (String) -> Unit = {},
    viewModel: CourseViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        uiState.course == null -> ErrorState(
            message = uiState.error ?: "Course not found",
            onRetry = {},
            modifier = Modifier.fillMaxSize(),
        )
        else -> {
            val course = uiState.course!!
            val tabs = listOf("Overview", "Curriculum", "Reviews", "Instructor")

            Scaffold(
                bottomBar = {
                    CourseEnrollBar(
                        course = course,
                        isEnrolled = uiState.isEnrolled,
                        isEnrolling = uiState.isEnrolling,
                        onEnroll = viewModel::enroll,
                        onStartLearning = { onStartLearning(course.id) },
                    )
                },
            ) { padding ->
                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                    // Thumbnail
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                            AsyncImage(
                                model = course.thumbnail_url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.statusBarsPadding().padding(8.dp),
                                colors = IconButtonDefaults.iconButtonColors(containerColor = NadjahGray900.copy(0.5f)),
                            ) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = NadjahWhite)
                            }
                            IconButton(
                                onClick = viewModel::toggleWishlist,
                                modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(8.dp),
                                colors = IconButtonDefaults.iconButtonColors(containerColor = NadjahGray900.copy(0.5f)),
                            ) {
                                Icon(
                                    if (uiState.isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Wishlist",
                                    tint = if (uiState.isWishlisted) NadjahRed500 else NadjahWhite,
                                )
                            }
                        }
                    }

                    // Course info header
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val badgeText = when {
                                course.is_bestseller -> "Bestseller"
                                course.is_featured -> "Featured"
                                else -> null
                            }
                            badgeText?.let {
                                Surface(color = NadjahGold100, shape = RoundedCornerShape(4.dp)) {
                                    Text(it, style = MaterialTheme.typography.labelSmall, color = NadjahGold900, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            Text(course.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(course.subtitle ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoChip(icon = Icons.Filled.Star, text = "${course.average_rating}", tint = StarYellow)
                                InfoChip(icon = Icons.Outlined.People, text = "${course.total_students} students")
                                InfoChip(icon = Icons.Outlined.AccessTime, text = "${course.total_duration / 3600}h")
                                InfoChip(icon = Icons.Outlined.SignalCellularAlt, text = course.level)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AsyncImage(
                                    model = course.instructor_avatar,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp).clip(CircleShape),
                                )
                                Text("By ${course.instructor_name ?: ""}", style = MaterialTheme.typography.bodySmall, color = NadjahRed600)
                            }
                        }
                        HorizontalDivider()
                    }

                    // Tabs
                    item {
                        ScrollableTabRow(
                            selectedTabIndex = uiState.selectedTab,
                            edgePadding = 16.dp,
                            containerColor = MaterialTheme.colorScheme.surface,
                        ) {
                            tabs.forEachIndexed { i, tab ->
                                Tab(
                                    selected = uiState.selectedTab == i,
                                    onClick = { viewModel.selectTab(i) },
                                    text = { Text(tab) },
                                )
                            }
                        }
                    }

                    // Tab content
                    when (uiState.selectedTab) {
                        0 -> { // Overview
                            item {
                                OverviewTab(course = course)
                            }
                        }
                        1 -> { // Curriculum
                            items(course.sections) { section ->
                                CurriculumSection(section = section)
                            }
                        }
                        2 -> { // Reviews
                            items(uiState.reviews) { review ->
                                ReviewItem(review = review)
                            }
                        }
                        3 -> { // Instructor
                            item {
                                val snap = course
                                InstructorTab(
                                    course = snap,
                                    onInstructorClick = { snap.instructor_id?.let { onNavigateToInstructor(it) } },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(course: CourseDetail) {
    Column(modifier = Modifier.padding(16.dp)) {
        // What you'll learn
        if (course.what_you_will_learn.isNotEmpty()) {
            Text("What you'll learn", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            course.what_you_will_learn.forEach { item ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = NadjahGreen500, modifier = Modifier.size(18.dp).padding(top = 2.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(item, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Requirements
        if (course.requirements.isNotEmpty()) {
            Text("Requirements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            course.requirements.forEach { req ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text("•", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 8.dp))
                    Text(req, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Description
        course.description?.let {
            Text("About This Course", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CurriculumSection(section: CourseSection) {
    var expanded by remember { mutableStateOf(section.sort_order == 1) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(section.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text("${section.lessons.size} lessons", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
        }
        if (expanded) {
            section.lessons.forEach { lesson ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        when (lesson.type) {
                            "video" -> Icons.Outlined.PlayCircleOutline
                            "quiz" -> Icons.Outlined.Quiz
                            else -> Icons.Outlined.Article
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(lesson.title, style = MaterialTheme.typography.bodyMedium)
                        if (lesson.duration > 0) {
                            Text("${lesson.duration / 60} min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    if (lesson.is_preview) {
                        Surface(color = NadjahGreen100, shape = RoundedCornerShape(4.dp)) {
                            Text("Preview", style = MaterialTheme.typography.labelSmall, color = NadjahGreen600, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun ReviewItem(review: Review) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = review.user_avatar,
                contentDescription = null,
                modifier = Modifier.size(36.dp).clip(CircleShape),
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(review.user_name ?: "Student", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Row {
                    repeat(5) { i ->
                        Icon(
                            if (i < review.rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
            Text(review.created_at, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(8.dp))
        Text(review.body ?: "", style = MaterialTheme.typography.bodyMedium)
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun InstructorTab(course: CourseDetail, onInstructorClick: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.clickable(onClick = onInstructorClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = course.instructor_avatar,
                contentDescription = null,
                modifier = Modifier.size(64.dp).clip(CircleShape),
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(course.instructor_name ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                course.instructor_headline?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
        Spacer(Modifier.height(12.dp))
        course.instructor_bio?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CourseEnrollBar(
    course: CourseDetail,
    isEnrolled: Boolean,
    isEnrolling: Boolean,
    onEnroll: () -> Unit,
    onStartLearning: () -> Unit,
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (course.original_price != null && course.original_price!! > course.price) {
                    Text(
                        "${course.original_price} DZD",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                    )
                }
                Text(
                    if (course.price == 0.0) "Free" else "${course.price} DZD",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NadjahCharcoal600,
                )
            }
            NadjahButton(
                text = if (isEnrolled) "Continue Learning" else if (course.price == 0.0) "Enroll Free" else "Buy Now",
                onClick = if (isEnrolled) onStartLearning else onEnroll,
                modifier = Modifier.weight(1f),
                loading = isEnrolling,
            )
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String, tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
