package dz.nadjahacademy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dz.nadjahacademy.app.navigation.NadjahNavHost
import dz.nadjahacademy.app.ui.SplashScreen
import dz.nadjahacademy.core.ui.theme.NadjahAcademyTheme
import dz.nadjahacademy.core.auth.AuthManager
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            val themePreference by authManager.themePreference.collectAsState(initial = "system")
            var showSplash by remember { mutableStateOf(true) }

            NadjahAcademyTheme(
                darkTheme = when (themePreference) {
                    "dark" -> true
                    "light" -> false
                    else -> isSystemInDarkTheme()
                }
            ) {
                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    NadjahNavHost(startDestination = "main", deepLinkIntent = intent)
                }
            }
        }
    }

    private fun isSystemInDarkTheme(): Boolean {
        return (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}
