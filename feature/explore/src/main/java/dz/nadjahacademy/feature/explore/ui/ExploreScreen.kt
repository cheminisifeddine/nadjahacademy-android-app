package dz.nadjahacademy.feature.explore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.explore.viewmodel.ExploreFilters
import dz.nadjahacademy.feature.explore.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val sortOptions = listOf("popular" to "Most Popular", "newest" to "Newest", "rating" to "Top Rated", "price_asc" to "Price: Low to High", "price_desc" to "Price: High to Low")

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar with search bar
        Surface(shadowElevation = 2.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Explore Courses", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                NadjahSearchBar(
                    query = "",
                    onQueryChange = {},
                    onSearch = {},
                    placeholder = "Search courses...",
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    onClick = onNavigateToSearch,
                )
            }
        }

        // Category filter row
        if (uiState.categories.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = uiState.filters.categorySlug == null,
                        onClick = { viewModel.setCategory(null) },
                        label = { Text("All") },
                    )
                }
                items(uiState.categories) { cat ->
                    FilterChip(
                        selected = uiState.filters.categorySlug == cat.slug,
                        onClick = { viewModel.setCategory(cat.slug) },
                        label = { Text(cat.name) },
                    )
                }
            }
        }

        // Sort + filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${uiState.totalCount} courses",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            // Sort dropdown
            var sortExpanded by remember { mutableStateOf(false) }
            val currentSortLabel = sortOptions.firstOrNull { it.first == uiState.filters.sortBy }?.second ?: "Sort"
            Box {
                TextButton(onClick = { sortExpanded = true }) {
                    Icon(Icons.Outlined.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(currentSortLabel, style = MaterialTheme.typography.labelMedium)
                }
                DropdownMenu(expanded = sortExpanded, onDismissRequest = { sortExpanded = false }) {
                    sortOptions.forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = { viewModel.setSortBy(value); sortExpanded = false },
                            leadingIcon = if (uiState.filters.sortBy == value) {
                                { Icon(Icons.Outlined.Check, contentDescription = null) }
                            } else null,
                        )
                    }
                }
            }
            // Filter button
            IconButton(onClick = viewModel::openFilterSheet) {
                BadgedBox(badge = {
                    val activeFilters = listOfNotNull(
                        uiState.filters.level,
                        uiState.filters.language,
                        uiState.filters.minRating?.takeIf { it > 0 },
                        uiState.filters.isFree,
                        uiState.filters.minPrice,
                        uiState.filters.maxPrice,
                    ).size
                    if (activeFilters > 0) Badge { Text("$activeFilters") }
                }) {
                    Icon(Icons.Outlined.Tune, contentDescription = "Filters")
                }
            }
        }

        HorizontalDivider()

        // Course grid / list
        if (uiState.isLoading) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(6) { CourseCardSkeleton() }
            }
        } else if (uiState.courses.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.SearchOff,
                title = "No Courses Found",
                message = "Try adjusting your filters or search terms",
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.courses, key = { it.id }) { course ->
                    CourseCardLarge(
                        title = course.title,
                        instructorName = course.instructor_name ?: "",
                        rating = course.average_rating,
                        reviewCount = course.total_reviews,
                        price = course.price,
                        originalPrice = course.original_price,
                        thumbnailUrl = course.thumbnail_url,
                        isBestseller = course.is_bestseller,
                        isNew = course.is_new,
                        onClick = { onNavigateToCourse(course.slug) },
                    )
                }
                if (uiState.isLoadingMore) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }
                } else if (uiState.hasNextPage) {
                    item(span = { GridItemSpan(2) }) {
                        LaunchedEffect(Unit) { viewModel.loadMore() }
                    }
                }
            }
        }
    }

    // Filter bottom sheet
    if (uiState.isFilterSheetOpen) {
        FilterBottomSheet(
            currentFilters = uiState.filters,
            onApply = viewModel::updateFilters,
            onDismiss = viewModel::closeFilterSheet,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    currentFilters: ExploreFilters,
    onApply: (ExploreFilters) -> Unit,
    onDismiss: () -> Unit,
) {
    var level by remember { mutableStateOf(currentFilters.level) }
    var language by remember { mutableStateOf(currentFilters.language) }
    var minRating by remember { mutableStateOf(currentFilters.minRating ?: 0.0) }
    var isFree by remember { mutableStateOf(currentFilters.isFree) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Filters", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                TextButton(onClick = {
                    level = null; language = null; minRating = 0.0; isFree = null
                }) { Text("Reset") }
            }
            Spacer(Modifier.height(16.dp))
            Text("Level", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("beginner", "intermediate", "advanced").forEach { lvl ->
                    FilterChip(
                        selected = level == lvl,
                        onClick = { level = if (level == lvl) null else lvl },
                        label = { Text(lvl.replaceFirstChar { it.uppercase() }) },
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Language", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Arabic" to "ar", "English" to "en", "French" to "fr").forEach { (label, code) ->
                    FilterChip(
                        selected = language == code,
                        onClick = { language = if (language == code) null else code },
                        label = { Text(label) },
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Minimum Rating: ${if (minRating > 0) "${"%.1f".format(minRating)}+" else "Any"}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Slider(value = minRating.toFloat(), onValueChange = { minRating = it.toDouble() }, valueRange = 0f..5f, steps = 9)

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Free Courses Only", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Switch(checked = isFree == true, onCheckedChange = { isFree = if (it) true else null })
            }

            Spacer(Modifier.height(24.dp))
            NadjahButton(
                text = "Apply Filters",
                onClick = {
                    onApply(currentFilters.copy(level = level, language = language, minRating = if (minRating > 0) minRating else null, isFree = isFree))
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
