package dz.nadjahacademy.feature.quiz.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.model.QuizResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class QuizResultUiState(
    val isLoading: Boolean = false,
    val result: QuizResult? = null,
    val error: String? = null,
)

@HiltViewModel
class QuizResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val attemptId: String = savedStateHandle["attemptId"] ?: ""

    private val _uiState = MutableStateFlow(QuizResultUiState())
    val uiState: StateFlow<QuizResultUiState> = _uiState.asStateFlow()
}
