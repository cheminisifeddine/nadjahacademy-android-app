package dz.nadjahacademy.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.auth.AuthManager
import dz.nadjahacademy.core.network.api.AuthApiService
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authApiService: AuthApiService,
    private val authManager: AuthManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val fieldErrors = mutableMapOf<String, String>()
            if (email.isBlank()) fieldErrors["email"] = "Email is required"
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) fieldErrors["email"] = "Invalid email"
            if (password.isBlank()) fieldErrors["password"] = "Password is required"
            if (fieldErrors.isNotEmpty()) { _uiState.value = AuthUiState(fieldErrors = fieldErrors); return@launch }

            _uiState.value = AuthUiState(isLoading = true)
            try {
                val resp = authApiService.login(LoginRequest(email = email.trim(), password = password))
                if (resp.isSuccessful) {
                    val data = resp.body()?.data!!
                    authManager.saveSession(
                        accessToken = data.access_token,
                        refreshToken = data.refresh_token,
                        userId = data.user.id,
                        email = data.user.email,
                        fullName = data.user.full_name,
                        role = data.user.role,
                        avatarUrl = data.user.avatar_url,
                        emailVerified = data.user.email_verified,
                        language = data.user.language,
                        learningStreak = data.user.learning_streak,
                        totalPoints = data.user.total_points,
                        level = data.user.level,
                    )
                    _uiState.value = AuthUiState(isSuccess = true)
                } else {
                    val msg = resp.errorBody()?.string() ?: "Login failed"
                    _uiState.value = AuthUiState(error = extractErrorMessage(msg))
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Network error")
            }
        }
    }

    fun register(email: String, password: String, fullName: String, phone: String?) {
        viewModelScope.launch {
            val fieldErrors = mutableMapOf<String, String>()
            if (fullName.isBlank()) fieldErrors["full_name"] = "Full name is required"
            if (email.isBlank()) fieldErrors["email"] = "Email is required"
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) fieldErrors["email"] = "Invalid email"
            if (password.length < 8) fieldErrors["password"] = "Password must be at least 8 characters"
            if (fieldErrors.isNotEmpty()) { _uiState.value = AuthUiState(fieldErrors = fieldErrors); return@launch }

            _uiState.value = AuthUiState(isLoading = true)
            try {
                val resp = authApiService.register(RegisterRequest(
                    email = email.trim(), password = password,
                    full_name = fullName.trim(), phone = phone?.ifBlank { null },
                    language = "ar",
                ))
                if (resp.isSuccessful) {
                    val data = resp.body()?.data!!
                    authManager.saveSession(
                        accessToken = data.access_token, refreshToken = data.refresh_token,
                        userId = data.user.id, email = data.user.email,
                        fullName = data.user.full_name, role = data.user.role,
                        avatarUrl = data.user.avatar_url, emailVerified = data.user.email_verified,
                        language = data.user.language,
                    )
                    _uiState.value = AuthUiState(isSuccess = true)
                } else {
                    val msg = resp.errorBody()?.string() ?: "Registration failed"
                    _uiState.value = AuthUiState(error = extractErrorMessage(msg))
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Network error")
            }
        }
    }

    fun socialLogin(provider: String, idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val resp = authApiService.socialLogin(SocialLoginRequest(provider = provider, id_token = idToken))
                if (resp.isSuccessful) {
                    val data = resp.body()?.data!!
                    authManager.saveSession(
                        accessToken = data.access_token, refreshToken = data.refresh_token,
                        userId = data.user.id, email = data.user.email,
                        fullName = data.user.full_name, role = data.user.role,
                        avatarUrl = data.user.avatar_url, emailVerified = data.user.email_verified,
                        language = data.user.language,
                    )
                    _uiState.value = AuthUiState(isSuccess = true)
                } else {
                    _uiState.value = AuthUiState(error = "Social login failed")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Network error")
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            if (email.isBlank()) { _uiState.value = AuthUiState(fieldErrors = mapOf("email" to "Email is required")); return@launch }
            _uiState.value = AuthUiState(isLoading = true)
            try {
                authApiService.forgotPassword(ForgotPasswordRequest(email.trim()))
                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Network error")
            }
        }
    }

    fun resetPassword(token: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            val fieldErrors = mutableMapOf<String, String>()
            if (password.length < 8) fieldErrors["password"] = "Password must be at least 8 characters"
            if (password != confirmPassword) fieldErrors["confirm_password"] = "Passwords do not match"
            if (fieldErrors.isNotEmpty()) { _uiState.value = AuthUiState(fieldErrors = fieldErrors); return@launch }

            _uiState.value = AuthUiState(isLoading = true)
            try {
                val resp = authApiService.resetPassword(ResetPasswordRequest(token = token, password = password))
                if (resp.isSuccessful) _uiState.value = AuthUiState(isSuccess = true)
                else _uiState.value = AuthUiState(error = "Invalid or expired reset link")
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Network error")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }

    fun resendVerification() {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                authApiService.resendVerification()
                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Network error")
            }
        }
    }

    private fun extractErrorMessage(body: String): String {
        return try {
            val json = org.json.JSONObject(body)
            json.getString("message")
        } catch (e: Exception) {
            "An error occurred. Please try again."
        }
    }
}
