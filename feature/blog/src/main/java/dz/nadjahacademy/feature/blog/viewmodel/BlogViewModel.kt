package dz.nadjahacademy.feature.blog.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.BlogsApiService
import dz.nadjahacademy.core.network.api.data
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BlogListUiState(
    val isLoading: Boolean = true,
    val posts: List<BlogListItem> = emptyList(),
    val page: Int = 1,
    val hasMore: Boolean = true,
    val selectedCategory: String? = null,
    val error: String? = null,
)

data class BlogDetailUiState(
    val isLoading: Boolean = true,
    val post: BlogDetail? = null,
    val error: String? = null,
)

@HiltViewModel
class BlogListViewModel @Inject constructor(
    private val blogsApi: BlogsApiService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BlogListUiState())
    val uiState: StateFlow<BlogListUiState> = _uiState.asStateFlow()
    init { loadPosts() }

    private fun loadPosts(page: Int = 1) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { blogsApi.getBlogs(page = page, limit = 20, category = _uiState.value.selectedCategory) }
                .onSuccess { response ->
                    val newPosts = response.data ?: emptyList()
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            posts = if (page == 1) newPosts else state.posts + newPosts,
                            page = page,
                            hasMore = newPosts.size == 20,
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun loadMore() { if (_uiState.value.hasMore && !_uiState.value.isLoading) loadPosts(_uiState.value.page + 1) }
    fun refresh() = loadPosts(1)
    fun filterByCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadPosts(1)
    }
}

@HiltViewModel
class BlogDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val blogsApi: BlogsApiService,
) : ViewModel() {
    private val postId: String = checkNotNull(savedStateHandle["postId"])
    private val _uiState = MutableStateFlow(BlogDetailUiState())
    val uiState: StateFlow<BlogDetailUiState> = _uiState.asStateFlow()
    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { blogsApi.getBlogDetail(postId) }
                .onSuccess { r -> _uiState.update { it.copy(isLoading = false, post = r.data) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
