package dz.nadjahacademy.feature.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.NotificationsApiService
import dz.nadjahacademy.core.network.api.data
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val notifications: List<Notification> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsApi: NotificationsApiService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()
    init { loadNotifications() }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { notificationsApi.getNotifications() }
                .onSuccess { r -> _uiState.update { it.copy(isLoading = false, notifications = r.data ?: emptyList()) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            runCatching { notificationsApi.markRead(MarkReadRequest(notification_id = notificationId)) }
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(notifications = state.notifications.map { n ->
                            if (n.id == notificationId) n.copy(is_read = true) else n
                        })
                    }
                }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            runCatching { notificationsApi.markRead(MarkReadRequest()) }
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(notifications = state.notifications.map { it.copy(is_read = true) })
                    }
                }
        }
    }
}
