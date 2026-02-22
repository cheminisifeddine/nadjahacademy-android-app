package dz.nadjahacademy.feature.mylearning.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.MyLearningApiService
import dz.nadjahacademy.core.network.api.data
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyLearningUiState(
    val isLoading: Boolean = true,
    val enrollments: List<EnrolledCourse> = emptyList(),
    val completedCourses: List<EnrolledCourse> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null,
)

@HiltViewModel
class MyLearningViewModel @Inject constructor(
    private val myLearningApi: MyLearningApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyLearningUiState())
    val uiState: StateFlow<MyLearningUiState> = _uiState.asStateFlow()

    init { loadEnrollments() }

    private fun loadEnrollments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { myLearningApi.getEnrolledCourses() }
                .onSuccess { response ->
                    val all = response.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            enrollments = all.filter { e -> e.progress_percentage < 100.0 },
                            completedCourses = all.filter { e -> e.progress_percentage >= 100.0 },
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun selectTab(index: Int) = _uiState.update { it.copy(selectedTab = index) }
    fun refresh() = loadEnrollments()
}
