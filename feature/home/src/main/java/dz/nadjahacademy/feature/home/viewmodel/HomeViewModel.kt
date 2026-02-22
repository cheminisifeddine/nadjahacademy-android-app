package dz.nadjahacademy.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.auth.AuthManager
import dz.nadjahacademy.core.network.api.CoursesApiService
import dz.nadjahacademy.core.network.api.MiscApiService
import dz.nadjahacademy.core.network.api.UsersApiService
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val greeting: String = "Welcome!",
    val userName: String = "",
    val streak: Int = 0,
    val totalPoints: Int = 0,
    val banners: List<Banner> = emptyList(),
    val featuredCourses: List<CourseListItem> = emptyList(),
    val popularCourses: List<CourseListItem> = emptyList(),
    val trendingCourses: List<CourseListItem> = emptyList(),
    val newCourses: List<CourseListItem> = emptyList(),
    val continueLearning: List<EnrolledCourse> = emptyList(),
    val categories: List<Category> = emptyList(),
    val testimonials: List<Testimonial> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val coursesApi: CoursesApiService,
    private val miscApi: MiscApiService,
    private val usersApi: UsersApiService,
    private val authManager: AuthManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadHomeData() }

    fun loadHomeData(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = !isRefreshing, isRefreshing = isRefreshing, error = null) }
            try {
                val featuredD = async { runCatching { coursesApi.getFeaturedCourses() } }
                val popularD = async { runCatching { coursesApi.getPopularCourses() } }
                val trendingD = async { runCatching { coursesApi.getTrendingCourses() } }
                val newD = async { runCatching { coursesApi.getNewCourses() } }
                val bannersD = async { runCatching { miscApi.getBanners("home") } }
                val testimonialsD = async { runCatching { miscApi.getTestimonials() } }
                val userSnapshot = authManager.authState.value
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                val timeGreet = when { hour < 12 -> "Good morning"; hour < 17 -> "Good afternoon"; else -> "Good evening" }
                val name = userSnapshot?.userName?.split(" ")?.firstOrNull() ?: ""
                _uiState.update { it.copy(
                    isLoading = false, isRefreshing = false,
                    greeting = if (name.isNotEmpty()) "$timeGreet, $name!" else "$timeGreet!",
                    userName = name,
                    streak = userSnapshot?.learningStreak ?: 0,
                    totalPoints = userSnapshot?.totalPoints ?: 0,
                    banners = bannersD.await().getOrNull()?.body()?.data ?: emptyList(),
                    featuredCourses = featuredD.await().getOrNull()?.body()?.data ?: emptyList(),
                    popularCourses = popularD.await().getOrNull()?.body()?.data ?: emptyList(),
                    trendingCourses = trendingD.await().getOrNull()?.body()?.data ?: emptyList(),
                    newCourses = newD.await().getOrNull()?.body()?.data ?: emptyList(),
                    testimonials = testimonialsD.await().getOrNull()?.body()?.data ?: emptyList(),
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = e.message) }
            }
        }
    }

    fun refresh() = loadHomeData(isRefreshing = true)
}
