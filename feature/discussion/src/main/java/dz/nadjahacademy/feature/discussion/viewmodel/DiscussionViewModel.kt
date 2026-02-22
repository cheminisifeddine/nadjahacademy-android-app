package dz.nadjahacademy.feature.discussion.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.DiscussionsApiService
import dz.nadjahacademy.core.network.api.data
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiscussionUiState(
    val isLoading: Boolean = true,
    val posts: List<Discussion> = emptyList(),
    val replyText: String = "",
    val isPosting: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class DiscussionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val discussionsApi: DiscussionsApiService,
) : ViewModel() {

    private val lessonId: String? = savedStateHandle["lessonId"]
    private val courseId: String? = savedStateHandle["courseId"]
    private val _uiState = MutableStateFlow(DiscussionUiState())
    val uiState: StateFlow<DiscussionUiState> = _uiState.asStateFlow()

    init { loadPosts() }

    private fun loadPosts() {
        if (courseId == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { discussionsApi.getDiscussions(courseId = courseId, lessonId = lessonId) }
                .onSuccess { r -> _uiState.update { it.copy(isLoading = false, posts = r.data ?: emptyList()) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun setReplyText(text: String) = _uiState.update { it.copy(replyText = text) }

    fun postMessage(parentId: String? = null) {
        val text = _uiState.value.replyText.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isPosting = true) }
            if (parentId == null) {
                runCatching {
                    discussionsApi.createDiscussion(
                        CreateDiscussionRequest(
                            course_id = courseId ?: "",
                            lesson_id = lessonId,
                            title = text.take(80),
                            body = text,
                        )
                    )
                }
                    .onSuccess { r ->
                        val newPost = r.data
                        _uiState.update { state ->
                            state.copy(
                                isPosting = false,
                                posts = if (newPost != null) state.posts + newPost else state.posts,
                                replyText = "",
                            )
                        }
                    }
                    .onFailure { e -> _uiState.update { it.copy(isPosting = false, error = e.message) } }
            } else {
                runCatching {
                    discussionsApi.addReply(parentId, AddReplyRequest(body = text))
                }
                    .onSuccess {
                        _uiState.update { it.copy(isPosting = false, replyText = "") }
                        loadPosts()
                    }
                    .onFailure { e -> _uiState.update { it.copy(isPosting = false, error = e.message) } }
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            runCatching { discussionsApi.vote(postId, VoteRequest()) }
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(posts = state.posts.map { p ->
                            if (p.id == postId) p.copy(upvotes = p.upvotes + 1) else p
                        })
                    }
                }
        }
    }
}
