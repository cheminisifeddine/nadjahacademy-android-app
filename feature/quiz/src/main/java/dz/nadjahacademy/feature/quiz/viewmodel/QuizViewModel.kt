package dz.nadjahacademy.feature.quiz.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.QuizzesApiService
import dz.nadjahacademy.core.network.api.data
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val isLoading: Boolean = true,
    val quiz: QuizDetail? = null,
    val attemptId: String? = null,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, String> = emptyMap(),
    val textAnswers: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    val result: QuizResult? = null,
    val error: String? = null,
    val timeRemainingSeconds: Int = 0,
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val quizzesApi: QuizzesApiService,
) : ViewModel() {

    private val quizId: String = checkNotNull(savedStateHandle["quizId"])
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init { loadQuiz() }

    private fun loadQuiz() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { quizzesApi.getQuiz(quizId) }
                .onSuccess { response ->
                    _uiState.update { it.copy(isLoading = false, quiz = response.data) }
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun startQuiz() {
        viewModelScope.launch {
            runCatching { quizzesApi.startQuiz(quizId) }
                .onSuccess { response ->
                    val timeLimitMinutes = _uiState.value.quiz?.time_limit ?: 0
                    _uiState.update { state ->
                        state.copy(
                            attemptId = response.data?.attempt_id,
                            timeRemainingSeconds = timeLimitMinutes * 60,
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun selectAnswer(questionId: String, optionId: String) {
        _uiState.update { state ->
            state.copy(selectedAnswers = state.selectedAnswers + (questionId to optionId))
        }
    }

    fun setTextAnswer(questionId: String, text: String) {
        _uiState.update { state ->
            state.copy(textAnswers = state.textAnswers + (questionId to text))
        }
    }

    fun goToQuestion(index: Int) = _uiState.update { it.copy(currentQuestionIndex = index) }

    fun nextQuestion() {
        val quiz = _uiState.value.quiz ?: return
        val next = _uiState.value.currentQuestionIndex + 1
        if (next < quiz.questions.size) {
            _uiState.update { it.copy(currentQuestionIndex = next) }
        }
    }

    fun previousQuestion() {
        val prev = _uiState.value.currentQuestionIndex - 1
        if (prev >= 0) _uiState.update { it.copy(currentQuestionIndex = prev) }
    }

    fun submitQuiz() {
        val attemptId = _uiState.value.attemptId ?: return
        val quiz = _uiState.value.quiz ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val answers = quiz.questions.map { q ->
                QuizAnswer(
                    question_id = q.id,
                    selected_option_ids = listOfNotNull(_uiState.value.selectedAnswers[q.id]),
                    text_answer = _uiState.value.textAnswers[q.id] ?: "",
                )
            }
            runCatching {
                quizzesApi.submitQuiz(quizId, QuizSubmitRequest(attempt_id = attemptId, answers = answers))
            }
                .onSuccess { response -> _uiState.update { it.copy(isSubmitting = false, result = response.data) } }
                .onFailure { e -> _uiState.update { it.copy(isSubmitting = false, error = e.message) } }
        }
    }

    fun onTimerTick() {
        val remaining = _uiState.value.timeRemainingSeconds
        if (remaining > 0) {
            _uiState.update { it.copy(timeRemainingSeconds = remaining - 1) }
        } else if (remaining == 0 && _uiState.value.attemptId != null && _uiState.value.result == null) {
            submitQuiz()
        }
    }
}
