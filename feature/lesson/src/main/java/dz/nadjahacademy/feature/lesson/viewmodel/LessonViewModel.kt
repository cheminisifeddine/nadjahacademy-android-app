package dz.nadjahacademy.feature.lesson.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.LessonsApiService
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonUiState(
    val isLoading: Boolean = true,
    val lesson: LessonDetail? = null,
    val notes: List<Note> = emptyList(),
    val currentPosition: Long = 0L,
    val isCompleted: Boolean = false,
    val isMarkingComplete: Boolean = false,
    val showNoteDialog: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val lessonsApi: LessonsApiService,
) : ViewModel() {
    private val lessonId: String = checkNotNull(savedStateHandle["lessonId"])
    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
    }

    fun loadLesson() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { lessonsApi.getLesson(lessonId) }
                .onSuccess { response ->
                    val lesson = response.body()?.data
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            lesson = lesson,
                            currentPosition = lesson?.progress?.watch_position?.toLong() ?: 0L,
                            isCompleted = lesson?.progress?.is_completed ?: false,
                        )
                    }
                    loadNotes()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            runCatching { lessonsApi.getNotes(lessonId) }
                .onSuccess { response ->
                    _uiState.update { it.copy(notes = response.body()?.data ?: emptyList()) }
                }
        }
    }

    fun onPositionChanged(positionMs: Long) {
        _uiState.update { it.copy(currentPosition = positionMs) }
    }

    fun saveProgress(positionMs: Long) {
        viewModelScope.launch {
            runCatching {
                lessonsApi.saveProgress(lessonId, LessonProgressRequest(position = (positionMs / 1000).toInt()))
            }
        }
    }

    fun markComplete() {
        if (_uiState.value.isCompleted || _uiState.value.isMarkingComplete) return
        viewModelScope.launch {
            _uiState.update { it.copy(isMarkingComplete = true) }
            runCatching { lessonsApi.completeLesson(lessonId, LessonProgressRequest(position = (_uiState.value.currentPosition / 1000).toInt())) }
                .onSuccess { _uiState.update { state -> state.copy(isMarkingComplete = false, isCompleted = true) } }
                .onFailure { _uiState.update { it.copy(isMarkingComplete = false) } }
        }
    }

    fun addNote(content: String, timestamp: Int?) {
        viewModelScope.launch {
            runCatching { lessonsApi.createNote(lessonId, CreateNoteRequest(content = content, timestamp = timestamp ?: 0)) }
                .onSuccess { loadNotes() }
        }
        _uiState.update { it.copy(showNoteDialog = false) }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            runCatching { lessonsApi.deleteNote(lessonId, noteId) }
                .onSuccess { loadNotes() }
        }
    }

    fun showNoteDialog() = _uiState.update { it.copy(showNoteDialog = true) }
    fun hideNoteDialog() = _uiState.update { it.copy(showNoteDialog = false) }
}
