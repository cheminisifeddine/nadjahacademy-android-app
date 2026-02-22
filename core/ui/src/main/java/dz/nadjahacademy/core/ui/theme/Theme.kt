package dz.nadjahacademy.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = NadjahRed600,
    onPrimary = NadjahWhite,
    primaryContainer = NadjahRed50,
    onPrimaryContainer = NadjahRed900,
    secondary = NadjahGold600,
    onSecondary = NadjahCharcoal800,
    secondaryContainer = NadjahGold50,
    onSecondaryContainer = NadjahGold900,
    tertiary = NadjahCharcoal800,
    onTertiary = NadjahWhite,
    tertiaryContainer = NadjahCharcoal50,
    onTertiaryContainer = NadjahCharcoal900,
    error = NadjahError500,
    onError = NadjahWhite,
    errorContainer = NadjahError100,
    onErrorContainer = NadjahError600,
    background = BackgroundLight,
    onBackground = NadjahGray900,
    surface = SurfaceLight,
    onSurface = NadjahGray900,
    surfaceVariant = NadjahRed50,
    onSurfaceVariant = NadjahGray600,
    outline = NadjahGray300,
    outlineVariant = NadjahGray200,
    inverseSurface = NadjahCharcoal800,
    inverseOnSurface = NadjahGray50,
    inversePrimary = NadjahRed300,
    surfaceTint = NadjahRed600,
    scrim = NadjahGray950,
)

private val DarkColorScheme = darkColorScheme(
    primary = NadjahRed400,
    onPrimary = NadjahRed900,
    primaryContainer = NadjahRed700,
    onPrimaryContainer = NadjahRed100,
    secondary = NadjahGold400,
    onSecondary = NadjahCharcoal900,
    secondaryContainer = NadjahGold900,
    onSecondaryContainer = NadjahGold100,
    tertiary = NadjahCharcoal300,
    onTertiary = NadjahCharcoal900,
    tertiaryContainer = NadjahCharcoal700,
    onTertiaryContainer = NadjahCharcoal100,
    error = NadjahError500,
    onError = NadjahWhite,
    errorContainer = NadjahError600,
    onErrorContainer = NadjahError100,
    background = BackgroundDark,
    onBackground = NadjahGray100,
    surface = SurfaceDark,
    onSurface = NadjahGray100,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = NadjahGray400,
    outline = NadjahCharcoal600,
    outlineVariant = NadjahCharcoal700,
    inverseSurface = NadjahGray100,
    inverseOnSurface = NadjahGray900,
    inversePrimary = NadjahRed600,
    surfaceTint = NadjahRed400,
    scrim = NadjahGray950,
)

@Composable
fun NadjahAcademyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = NadjahTypography,
        content = content,
    )
}
