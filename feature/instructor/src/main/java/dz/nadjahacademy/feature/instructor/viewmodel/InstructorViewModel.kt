package dz.nadjahacademy.feature.instructor.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.InstructorsApiService
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InstructorUiState(
    val isLoading: Boolean = true,
    val instructor: InstructorDetail? = null,
    val courses: List<CourseListItem> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class InstructorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val instructorsApi: InstructorsApiService,
) : ViewModel() {

    private val instructorId: String = checkNotNull(savedStateHandle["instructorId"])
    private val _uiState = MutableStateFlow(InstructorUiState())
    val uiState: StateFlow<InstructorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val profile = instructorsApi.getInstructorDetail(instructorId)
                val instructorData = profile.body()?.data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        instructor = instructorData,
                        courses = instructorData?.courses ?: emptyList(),
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
