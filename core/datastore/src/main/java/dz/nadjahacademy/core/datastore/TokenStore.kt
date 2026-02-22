package dz.nadjahacademy.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nadjah_prefs")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_full_name")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_AVATAR = stringPreferencesKey("user_avatar")
        val EMAIL_VERIFIED = booleanPreferencesKey("email_verified")
        val LANGUAGE = stringPreferencesKey("language")
        val THEME = stringPreferencesKey("theme")
        val ONBOARDING_SHOWN = booleanPreferencesKey("onboarding_shown")
        val LEARNING_STREAK = intPreferencesKey("learning_streak")
        val TOTAL_POINTS = intPreferencesKey("total_points")
        val LEVEL = intPreferencesKey("level")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val LAST_TOKEN_REFRESH = longPreferencesKey("last_token_refresh")
        val VIDEO_QUALITY = stringPreferencesKey("video_quality")
        val AUTOPLAY_NEXT = booleanPreferencesKey("autoplay_next")
        val DOWNLOAD_WIFI_ONLY = booleanPreferencesKey("download_wifi_only")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
            prefs[Keys.LAST_TOKEN_REFRESH] = System.currentTimeMillis()
        }
    }

    suspend fun saveUser(
        id: String, email: String, fullName: String, role: String,
        avatarUrl: String?, emailVerified: Boolean, language: String,
        learningStreak: Int, totalPoints: Int, level: Int,
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = id
            prefs[Keys.USER_EMAIL] = email
            prefs[Keys.USER_NAME] = fullName
            prefs[Keys.USER_ROLE] = role
            prefs[Keys.EMAIL_VERIFIED] = emailVerified
            prefs[Keys.LANGUAGE] = language
            prefs[Keys.LEARNING_STREAK] = learningStreak
            prefs[Keys.TOTAL_POINTS] = totalPoints
            prefs[Keys.LEVEL] = level
            if (avatarUrl != null) prefs[Keys.USER_AVATAR] = avatarUrl
        }
    }

    suspend fun getAccessToken(): String? =
        context.dataStore.data.catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { it[Keys.ACCESS_TOKEN] }.firstOrNull()

    suspend fun getRefreshToken(): String? =
        context.dataStore.data.map { it[Keys.REFRESH_TOKEN] }.firstOrNull()

    fun getUserIdFlow(): Flow<String?> = context.dataStore.data.map { it[Keys.USER_ID] }
    fun getLanguageFlow(): Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "ar" }
    fun getThemeFlow(): Flow<String> = context.dataStore.data.map { it[Keys.THEME] ?: "system" }
    fun isBiometricEnabledFlow(): Flow<Boolean> = context.dataStore.data.map { it[Keys.BIOMETRIC_ENABLED] ?: false }
    fun isOnboardingShownFlow(): Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_SHOWN] ?: false }
    fun getVideoQualityFlow(): Flow<String> = context.dataStore.data.map { it[Keys.VIDEO_QUALITY] ?: "auto" }
    fun isAutoplayNextFlow(): Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTOPLAY_NEXT] ?: true }
    fun isDownloadWifiOnlyFlow(): Flow<Boolean> = context.dataStore.data.map { it[Keys.DOWNLOAD_WIFI_ONLY] ?: true }

    fun isLoggedInFlow(): Flow<Boolean> = context.dataStore.data.map { it[Keys.ACCESS_TOKEN] != null }

    suspend fun getUserId(): String? = context.dataStore.data.map { it[Keys.USER_ID] }.firstOrNull()
    suspend fun getUserRole(): String = context.dataStore.data.map { it[Keys.USER_ROLE] ?: "student" }.firstOrNull() ?: "student"
    suspend fun getLanguage(): String = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "ar" }.firstOrNull() ?: "ar"

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { it[Keys.THEME] = theme }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun setOnboardingShown() {
        context.dataStore.edit { it[Keys.ONBOARDING_SHOWN] = true }
    }

    suspend fun setVideoQuality(quality: String) {
        context.dataStore.edit { it[Keys.VIDEO_QUALITY] = quality }
    }

    suspend fun setAutoplayNext(autoplay: Boolean) {
        context.dataStore.edit { it[Keys.AUTOPLAY_NEXT] = autoplay }
    }

    suspend fun setDownloadWifiOnly(wifiOnly: Boolean) {
        context.dataStore.edit { it[Keys.DOWNLOAD_WIFI_ONLY] = wifiOnly }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.USER_EMAIL)
            prefs.remove(Keys.USER_NAME)
            prefs.remove(Keys.USER_ROLE)
            prefs.remove(Keys.USER_AVATAR)
            prefs.remove(Keys.LEARNING_STREAK)
            prefs.remove(Keys.TOTAL_POINTS)
            prefs.remove(Keys.LEVEL)
        }
    }

    data class UserSnapshot(
        val id: String,
        val email: String,
        val fullName: String,
        val role: String,
        val avatarUrl: String?,
        val emailVerified: Boolean,
        val language: String,
        val learningStreak: Int,
        val totalPoints: Int,
        val level: Int,
    )

    fun getUserFlow(): Flow<UserSnapshot?> = context.dataStore.data.map { prefs ->
        val id = prefs[Keys.USER_ID] ?: return@map null
        UserSnapshot(
            id = id,
            email = prefs[Keys.USER_EMAIL] ?: "",
            fullName = prefs[Keys.USER_NAME] ?: "",
            role = prefs[Keys.USER_ROLE] ?: "student",
            avatarUrl = prefs[Keys.USER_AVATAR],
            emailVerified = prefs[Keys.EMAIL_VERIFIED] ?: false,
            language = prefs[Keys.LANGUAGE] ?: "ar",
            learningStreak = prefs[Keys.LEARNING_STREAK] ?: 0,
            totalPoints = prefs[Keys.TOTAL_POINTS] ?: 0,
            level = prefs[Keys.LEVEL] ?: 1,
        )
    }
}
