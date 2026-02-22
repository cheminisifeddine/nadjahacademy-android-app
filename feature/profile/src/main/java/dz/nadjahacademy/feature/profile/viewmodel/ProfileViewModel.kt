package dz.nadjahacademy.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.auth.AuthManager
import dz.nadjahacademy.core.network.api.UsersApiService
import dz.nadjahacademy.core.network.api.data
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: UserProfile? = null,
    val stats: UserStats? = null,
    val certificates: List<Certificate> = emptyList(),
    val error: String? = null,
    val isLoggingOut: Boolean = false,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val usersApi: UsersApiService,
    private val authManager: AuthManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init { loadProfile() }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val profileDeferred = async { usersApi.getMe() }
                val statsDeferred = async { runCatching { usersApi.getStats() }.getOrNull() }
                val certsDeferred = async { runCatching { usersApi.getCertificates() }.getOrNull() }

                val profile = profileDeferred.await()
                val stats = statsDeferred.await()
                val certs = certsDeferred.await()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = profile.data,
                        stats = stats?.data,
                        certificates = certs?.data ?: emptyList(),
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            authManager.logout()
        }
    }

    fun refresh() = loadProfile()
}
