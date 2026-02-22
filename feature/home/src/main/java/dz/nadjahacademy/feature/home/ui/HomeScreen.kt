package dz.nadjahacademy.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dz.nadjahacademy.core.network.model.*
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.home.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToBlog: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToInstructor: (String) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToLesson: (String, String) -> Unit,
    onSeeAllCourses: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp),
    ) {
        // Top bar
        item {
            Surface(shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Nadjah Academy",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NadjahCharcoal600,
                        )
                        Text(
                            "What will you learn today?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(badge = {}) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                        }
                    }
                }
            }
        }

        // Banner carousel
        if (uiState.banners.isNotEmpty()) {
            item {
                BannerCarousel(banners = uiState.banners, onBannerClick = { })
                Spacer(Modifier.height(16.dp))
            }
        }

        // Continue Learning
        if (uiState.continueLearning.isNotEmpty()) {
            item {
                SectionHeader(title = "Continue Learning", onSeeAll = { onSeeAllCourses("enrolled") })
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.continueLearning) { enrollment ->
                        ContinueLearningCard(enrollment = enrollment, onClick = {
                            onNavigateToCourse(enrollment.course_id)
                        })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // Categories
        if (uiState.categories.isNotEmpty()) {
            item {
                SectionHeader(title = "Categories", onSeeAll = null)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.categories) { cat ->
                        CategoryChip(category = cat, onClick = { onSeeAllCourses("category:${cat.id}") })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // Featured courses
        if (uiState.featuredCourses.isNotEmpty()) {
            item { SectionHeader(title = "Featured Courses", onSeeAll = { onSeeAllCourses("featured") }) }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.featuredCourses) { course ->
                        CourseCard(course = course, onClick = { onNavigateToCourse(course.id) })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // Popular courses
        if (uiState.popularCourses.isNotEmpty()) {
            item { SectionHeader(title = "Most Popular", onSeeAll = { onSeeAllCourses("popular") }) }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.popularCourses) { course ->
                        CourseCard(course = course, onClick = { onNavigateToCourse(course.id) })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // New arrivals
        if (uiState.newCourses.isNotEmpty()) {
            item { SectionHeader(title = "New Arrivals", onSeeAll = { onSeeAllCourses("new") }) }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.newCourses) { course ->
                        CourseCard(course = course, onClick = { onNavigateToCourse(course.id) })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // Stats banner
        item {
            StatsBanner()
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BannerCarousel(banners: List<Banner>, onBannerClick: (Banner) -> Unit) {
    val pagerState = rememberPagerState { banners.size }
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val next = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(next)
        }
    }
    Box {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().height(180.dp)) { page ->
            val banner = banners[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onBannerClick(banner) }
                    .background(NadjahCharcoal600),
            ) {
                AsyncImage(
                    model = banner.image_url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(NadjahCharcoal900.copy(alpha = 0.7f), NadjahCharcoal900.copy(alpha = 0.1f)))),
                )
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Text(banner.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NadjahWhite)
                    banner.subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = NadjahWhite.copy(0.8f)) }
                }
            }
        }
        // Page indicators
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            repeat(banners.size) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == pagerState.currentPage) 16.dp else 6.dp, 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (i == pagerState.currentPage) NadjahRed600 else NadjahGray400),
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: (() -> Unit)?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (onSeeAll != null) {
            TextButton(onClick = onSeeAll) {
                Text("See all", style = MaterialTheme.typography.labelMedium, color = NadjahRed600)
            }
        }
    }
}

@Composable
private fun CourseCard(course: CourseListItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(200.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            AsyncImage(
                model = course.thumbnail_url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(110.dp),
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(course.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(course.instructor_name ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = StarYellow, modifier = Modifier.size(14.dp))
                    Text(" ${course.average_rating}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Text(
                        if (course.price == 0.0 || course.price == null) "Free" else "${course.price} DZD",
                        style = MaterialTheme.typography.labelMedium,
                        color = NadjahCharcoal600,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ContinueLearningCard(enrollment: EnrolledCourse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(260.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Box {
                AsyncImage(
                    model = enrollment.thumbnail_url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                )
                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(NadjahGray900.copy(0.6f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    val progress = enrollment.progress_percentage.toInt()
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Progress", style = MaterialTheme.typography.labelSmall, color = NadjahWhite)
                            Text("$progress%", style = MaterialTheme.typography.labelSmall, color = NadjahWhite, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(3.dp))
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = NadjahGold400,
                            trackColor = NadjahGray600,
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(enrollment.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Continue where you left off", style = MaterialTheme.typography.bodySmall, color = NadjahRed600)
            }
        }
    }
}

@Composable
private fun CategoryChip(category: Category, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = NadjahRed50,
        border = androidx.compose.foundation.BorderStroke(1.dp, NadjahRed200),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(category.name, style = MaterialTheme.typography.labelMedium, color = NadjahCharcoal700)
        }
    }
}

@Composable
private fun StatsBanner() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NadjahCharcoal600),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatItem("50K+", "Students")
            VerticalDivider(modifier = Modifier.height(40.dp), color = NadjahWhite.copy(0.3f))
            StatItem("200+", "Courses")
            VerticalDivider(modifier = Modifier.height(40.dp), color = NadjahWhite.copy(0.3f))
            StatItem("50+", "Instructors")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = NadjahWhite)
        Text(label, style = MaterialTheme.typography.bodySmall, color = NadjahWhite.copy(0.8f))
    }
}

@Composable
private fun BlogPreviewCard(blog: BlogListItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = blog.cover_url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(blog.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(blog.excerpt ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
