package dz.nadjahacademy.feature.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.SearchApiService
import dz.nadjahacademy.core.network.api.data
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val courses: List<CourseListItem> = emptyList(),
    val instructors: List<InstructorListItem> = emptyList(),
    val error: String? = null,
    val hasSearched: Boolean = false,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchApi: SearchApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(400)
                .filter { it.length >= 2 }
                .distinctUntilChanged()
                .collect { query -> search(query) }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        queryFlow.value = query
        if (query.length < 2) {
            _uiState.update { it.copy(courses = emptyList(), instructors = emptyList(), hasSearched = false) }
        }
    }

    fun clearQuery() = onQueryChange("")

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { searchApi.search(query) }
                .onSuccess { r ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            courses = r.data?.courses ?: emptyList(),
                            instructors = r.data?.instructors ?: emptyList(),
                            hasSearched = true,
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
