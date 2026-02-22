package dz.nadjahacademy.core.auth

import dz.nadjahacademy.core.datastore.TokenStore
import dz.nadjahacademy.core.network.api.AuthApiService
import dz.nadjahacademy.core.network.model.RefreshTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class AuthState(
    val isLoggedIn: Boolean = false,
    val userId: String? = null,
    val userEmail: String = "",
    val userName: String = "",
    val userRole: String = "student",
    val userAvatar: String? = null,
    val emailVerified: Boolean = false,
    val language: String = "ar",
    val learningStreak: Int = 0,
    val totalPoints: Int = 0,
    val level: Int = 1,
)

@Singleton
class AuthManager @Inject constructor(
    private val tokenStore: TokenStore,
    private val authApiService: AuthApiService,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _authState = MutableStateFlow<AuthState?>(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    val themePreference = tokenStore.getThemeFlow()

    init {
        tokenStore.getUserFlow()
            .onEach { user ->
                _authState.value = if (user != null) {
                    AuthState(
                        isLoggedIn = true,
                        userId = user.id,
                        userEmail = user.email,
                        userName = user.fullName,
                        userRole = user.role,
                        userAvatar = user.avatarUrl,
                        emailVerified = user.emailVerified,
                        language = user.language,
                        learningStreak = user.learningStreak,
                        totalPoints = user.totalPoints,
                        level = user.level,
                    )
                } else {
                    AuthState(isLoggedIn = false)
                }
            }
            .launchIn(scope)
    }

    suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        fullName: String,
        role: String,
        avatarUrl: String?,
        emailVerified: Boolean,
        language: String,
        learningStreak: Int = 0,
        totalPoints: Int = 0,
        level: Int = 1,
    ) {
        tokenStore.saveTokens(accessToken, refreshToken)
        tokenStore.saveUser(userId, email, fullName, role, avatarUrl, emailVerified, language, learningStreak, totalPoints, level)
    }

    suspend fun refreshTokens(): Boolean {
        return try {
            val refreshToken = tokenStore.getRefreshToken() ?: return false
            val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful) {
                val tokenResp = response.body()?.data ?: return false
                tokenStore.saveTokens(tokenResp.access_token, tokenResp.refresh_token)
                true
            } else {
                logout()
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun logout() {
        try {
            val refreshToken = tokenStore.getRefreshToken()
            if (refreshToken != null) {
                authApiService.logout(dz.nadjahacademy.core.network.model.LogoutRequest(refreshToken))
            }
        } catch (e: Exception) {
            // Ignore network errors on logout
        }
        tokenStore.clearTokens()
    }

    fun isLoggedIn(): Boolean = _authState.value?.isLoggedIn == true
    fun getUserId(): String? = _authState.value?.userId
    fun getUserRole(): String = _authState.value?.userRole ?: "student"
    fun isAdmin(): Boolean = getUserRole() == "admin"
    fun isInstructor(): Boolean = getUserRole() == "instructor"
}
