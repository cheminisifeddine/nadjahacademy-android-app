package dz.nadjahacademy.feature.course.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.auth.AuthManager
import dz.nadjahacademy.core.network.api.CoursesApiService
import dz.nadjahacademy.core.network.api.MyLearningApiService
import dz.nadjahacademy.core.network.api.data
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseDetailUiState(
    val isLoading: Boolean = true,
    val course: CourseDetail? = null,
    val isEnrolled: Boolean = false,
    val enrollment: EnrollmentInfo? = null,
    val reviews: List<Review> = emptyList(),
    val selectedTab: Int = 0,
    val isEnrolling: Boolean = false,
    val error: String? = null,
    val isWishlisted: Boolean = false,
)

@HiltViewModel
class CourseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val coursesApi: CoursesApiService,
    private val myLearningApi: MyLearningApiService,
    private val authManager: AuthManager,
) : ViewModel() {

    private val courseId: String = checkNotNull(savedStateHandle["courseId"])
    private val _uiState = MutableStateFlow(CourseDetailUiState())
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    init { loadCourseDetail() }

    private fun loadCourseDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val courseDeferred = async { coursesApi.getCourseDetail(courseId) }
                val reviewsDeferred = async { coursesApi.getCourseReviews(courseId, 1, 20) }

                val courseResponse = courseDeferred.await()
                val reviewsResponse = reviewsDeferred.await()
                val course = courseResponse.data

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        course = course,
                        reviews = reviewsResponse.data ?: emptyList(),
                        isEnrolled = course?.is_enrolled == true,
                        enrollment = course?.enrollment,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectTab(index: Int) = _uiState.update { it.copy(selectedTab = index) }

    fun enroll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isEnrolling = true) }
            runCatching { coursesApi.enrollFree(courseId) }
                .onSuccess {
                    _uiState.update { it.copy(isEnrolling = false, isEnrolled = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isEnrolling = false, error = e.message) }
                }
        }
    }

    fun toggleWishlist() {
        viewModelScope.launch {
            runCatching { coursesApi.toggleBookmark(courseId) }
                .onSuccess { response ->
                    val newState = response.data?.bookmarked ?: !_uiState.value.isWishlisted
                    _uiState.update { it.copy(isWishlisted = newState) }
                }
        }
    }

    fun shareUrl(): String = "https://nadjahacademy.com/courses/$courseId"
}
