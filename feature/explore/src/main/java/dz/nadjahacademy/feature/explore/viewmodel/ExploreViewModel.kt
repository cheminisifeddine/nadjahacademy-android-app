package dz.nadjahacademy.feature.explore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.CategoriesApiService
import dz.nadjahacademy.core.network.api.CoursesApiService
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreFilters(
    val categorySlug: String? = null,
    val level: String? = null,
    val language: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minRating: Double? = null,
    val isFree: Boolean? = null,
    val sortBy: String = "popular",
)

data class ExploreUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val courses: List<CourseListItem> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filters: ExploreFilters = ExploreFilters(),
    val totalCount: Int = 0,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = false,
    val error: String? = null,
    val isFilterSheetOpen: Boolean = false,
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val coursesApi: CoursesApiService,
    private val categoriesApi: CategoriesApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadCourses(reset = true)
    }

    private fun loadCategories() {
        viewModelScope.launch {
            runCatching { categoriesApi.getCategories() }
                .onSuccess { response ->
                    _uiState.update { it.copy(categories = response.body()?.data ?: emptyList()) }
                }
        }
    }

    fun loadCourses(reset: Boolean = false) {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore) return
        val page = if (reset) 1 else _uiState.value.currentPage + 1
        viewModelScope.launch {
            _uiState.update { state ->
                if (reset) state.copy(isLoading = true, error = null, courses = emptyList())
                else state.copy(isLoadingMore = true)
            }
            val filters = _uiState.value.filters
            runCatching {
                coursesApi.getCourses(
                    page = page,
                    limit = 20,
                    category = filters.categorySlug,
                    level = filters.level,
                    language = filters.language,
                    minPrice = filters.minPrice,
                    maxPrice = filters.maxPrice,
                    minRating = filters.minRating,
                    isFree = filters.isFree,
                    sort = filters.sortBy,
                )
            }.onSuccess { response ->
                _uiState.update { state ->
                    val responseData = response.body()?.data ?: emptyList()
                    state.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        courses = if (reset) responseData else state.courses + responseData,
                        totalCount = response.body()?.pagination?.total ?: state.totalCount,
                        currentPage = page,
                        hasNextPage = response.body()?.pagination?.let { it.page < it.total_pages } ?: false,
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, isLoadingMore = false, error = e.message) }
            }
        }
    }

    fun updateFilters(filters: ExploreFilters) {
        _uiState.update { it.copy(filters = filters, isFilterSheetOpen = false) }
        loadCourses(reset = true)
    }

    fun setCategory(slug: String?) {
        val newFilters = _uiState.value.filters.copy(categorySlug = slug)
        _uiState.update { it.copy(filters = newFilters) }
        loadCourses(reset = true)
    }

    fun setSortBy(sort: String) {
        val newFilters = _uiState.value.filters.copy(sortBy = sort)
        _uiState.update { it.copy(filters = newFilters) }
        loadCourses(reset = true)
    }

    fun openFilterSheet() = _uiState.update { it.copy(isFilterSheetOpen = true) }
    fun closeFilterSheet() = _uiState.update { it.copy(isFilterSheetOpen = false) }

    fun loadMore() {
        if (_uiState.value.hasNextPage && !_uiState.value.isLoadingMore) {
            loadCourses(reset = false)
        }
    }
}
