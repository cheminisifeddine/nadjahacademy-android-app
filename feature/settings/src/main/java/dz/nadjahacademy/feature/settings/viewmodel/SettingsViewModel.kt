package dz.nadjahacademy.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.datastore.TokenStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val language: String = "en",
    val notificationsEnabled: Boolean = true,
    val downloadOverWifiOnly: Boolean = true,
    val autoPlayNext: Boolean = true,
    val videoQuality: String = "auto",
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenStore: TokenStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val theme = tokenStore.getThemeFlow().firstOrNull() ?: "system"
            val lang = tokenStore.getLanguage()
            _uiState.update { it.copy(isDarkTheme = theme == "dark", language = lang) }
        }
    }

    fun toggleDarkTheme() {
        val newValue = !_uiState.value.isDarkTheme
        _uiState.update { it.copy(isDarkTheme = newValue) }
        viewModelScope.launch { tokenStore.setTheme(if (newValue) "dark" else "light") }
    }

    fun setLanguage(lang: String) {
        _uiState.update { it.copy(language = lang) }
        viewModelScope.launch { tokenStore.setLanguage(lang) }
    }

    fun toggleNotifications() = _uiState.update { it.copy(notificationsEnabled = !it.notificationsEnabled) }
    fun toggleWifiOnly() = _uiState.update { it.copy(downloadOverWifiOnly = !it.downloadOverWifiOnly) }
    fun toggleAutoPlay() = _uiState.update { it.copy(autoPlayNext = !it.autoPlayNext) }
    fun setVideoQuality(quality: String) = _uiState.update { it.copy(videoQuality = quality) }
}
